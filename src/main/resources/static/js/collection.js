//LOAD USERS COLLECTION--------------------------------------------------------------------------------------------
async function loadCollection(){ //fetch is set to GET default, only changes if told
    const filter = document.getElementById("collectionView").value;
    const response = await fetch(`/api/usergames/user/collection?view=${filter}`, {
        credentials: "include"
    });

    if(!response.ok){
        window.location.href = "index.html";
        return;
    }

    const collection = await response.json();
    const container = document.getElementById("collectionContainer");
    container.innerHTML = "";

    collection.forEach(userGameEntry => createCard(userGameEntry));

}

//BUILD STAR FROM RATING VALUE -------------------------------------------------------------

function renderStars(rating){
    if(rating == null){
        return "Not rated yet";
    }

    let starsHTML ="";
    for(let i = 1; i <=5; i++){
        const filledClass = i <= rating ? "filled" : "";
        starsHTML += `<span class="star ${filledClass}">★</span>`
    }
    return starsHTML;
}


//BUILD A CARD FROM A USERGAME userGameEntry ---------------------------------------------------------------------

function createCard(userGameEntry){

    const container = document.getElementById("collectionContainer");

    const card = document.createElement("div");
    card.className = "gameCard";
    card.dataset.id = userGameEntry.id;

    card.innerHTML = `
            <div class="cardImage">
                <img src="${userGameEntry.coverURL}">
            </div>
            <h3 class="cardTitle">${userGameEntry.title}</h3>
            <p class="cardStatus">${userGameEntry.status}</p>
            <div class="cardRating">${renderStars(userGameEntry.rating)}</div>
            <p class="cardReview">${userGameEntry.review ?? "No review yet"}</p>
            <div class="cardButtons">
                <button class="editbtn">Edit</button>
                <button class="deletebtn">Delete</button>
            </div>
            
        `;

    const editbtn = card.querySelector(".editbtn");
    editbtn.addEventListener("click", () => openEditDialog(userGameEntry, card));

    const deletebtn = card.querySelector(".deletebtn");
    deletebtn.addEventListener("click", async () => {

        const confirmDelete = confirm("Are you sure you want to delete?");
        if(confirmDelete){
            const response = await fetch(`/api/usergames/${userGameEntry.id}`, {
                method: "DELETE",
                credentials: "include"
            });

            if(response.ok){
                card.remove();
            }else{
                alert("Failed to delete");
            }
        }

    })

    const img = card.querySelector("img");
    img.addEventListener("error", () => {
        img.src = "placeholder.png";
    });
    img.src = userGameEntry.coverURL ?? "placeholder.png"; //set src after attaching listener

    container.appendChild(card);
}

//EDIT GAME CARD ------------------------------------------------------------------------------------------

const editGameDialog = document.getElementById("editGameDialog");
const editGameForm = document.getElementById("editGameForm");
let editingUserGameEntry = null; //declared outside function to keep track of which userGameEntry and card is being edited
let editingCard = null;

function openEditDialog(userGameEntry, card){
    editingUserGameEntry = userGameEntry;
    editingCard = card;

    document.getElementById("editTitle").value = userGameEntry.title;
    document.getElementById("editCoverURL").value = userGameEntry.coverURL;
    document.getElementById("editReview").value = userGameEntry.review;
    document.getElementById("editRating").value = userGameEntry.rating ?? "";
    document.querySelector(`input[name="editStatus"][value="${userGameEntry.status}"]`).checked = true;
    //cant just set value on radio group, instead find specific radio whose value matches current entrys and set checked=true on it

    showCurrentRating("#editStarRating", userGameEntry.rating);
    //makes star display visually match the saved rating

    editGameDialog.showModal();
}

function closeEditDialog(){
    editGameDialog.close();
    editingUserGameEntry = null;
    editingCard = null;
}

const cancelEditGamebtn = document.getElementById("cancelEditGamebtn");
cancelEditGamebtn.addEventListener("click", closeEditDialog);

editGameForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const title = document.getElementById("editTitle").value;
    const coverURL = document.getElementById("editCoverURL").value;
    const status = document.querySelector(`input[name="editStatus"]:checked`).value; //get value from the radio input
    const rating = document.getElementById("editRating").value;
    const review = document.getElementById("editReview").value;

    const response = await fetch(`/api/usergames/${editingUserGameEntry.id}`, {
        method: "PUT",
        headers: {"Content-Type": "application/json"},
        credentials: "include",
        body: JSON.stringify({title, coverURL, status, rating: rating ? Number(rating) : null, review: review || null})
    });

    if(response.ok){
        const updatedUserGameEntry = await response.json();
        editingCard.querySelector(".cardTitle").textContent = updatedUserGameEntry.title;
        editingCard.querySelector(".cardStatus").textContent = updatedUserGameEntry.status;
        editingCard.querySelector(".cardRating").innerHTML = renderStars(updatedUserGameEntry.rating);
        editingCard.querySelector(".cardReview").textContent = updatedUserGameEntry.review;
        editingCard.querySelector("img").src = updatedUserGameEntry.coverURL ?? "placeholder.png";
        closeEditDialog();
        editGameForm.reset();
    }else{
        alert("Failed to update game");
    }
})




//DISPLAY GAME ADD MODAL-----------------------------------------------------------------------
const addGameDialog = document.getElementById("addGameDialog");

function openGameDialog(){
    addGameDialog.showModal();
}
function closeGameDialogue(){
    addGameDialog.close();
}

const openGameDialogbtn = document.getElementById("openGameDialogbtn");
openGameDialogbtn.addEventListener("click", openGameDialog);

const cancelAddGamebtn = document.getElementById("cancelAddGamebtn");
cancelAddGamebtn.addEventListener("click", closeGameDialogue);


//ADD GAME FORM SUBMIT---------------------------------------------------------------------------

const addGameForm = document.getElementById("addGameForm");
addGameForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const coverURL = document.getElementById("gameCoverURL").value;
    const title = document.getElementById("gameTitle").value;
    const status = document.querySelector('input[name="gameStatus"]:checked').value; //of all elements named gameStatus(both radio options), give me whichever is checked
    const rating = document.getElementById("gameRating").value;
    const review = document.getElementById("gameReview").value;

    const response = await fetch("/api/usergames", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        credentials: "include",
        body: JSON.stringify({title, coverURL, status, rating: rating ? Number(rating) : null, review: review || null})
    });

    if(response.ok){
        const newuserGameEntry = await response.json();

        const submitBtn = addGameForm.querySelector('button[type="submit"]');
        submitBtn.classList.add("success"); //If response is ok, add the success class to the button, which shows confirmation

        setTimeout(() => {
            submitBtn.classList.remove("success") //after delay (to allow show of confirmation), reset dialog
            createCard(newuserGameEntry);
            closeGameDialogue();
            addGameForm.reset();
        }, 400);
    }else{
        alert("Failed to add game");
    }
})


//STAR RATING SYSTEM ---------------------------------------------------------------------------
// Attaches click behavior to one star widget, scoped to a single container so
// multiple star widgets (add dialog, edit dialog) can exist independently on
// the same page without their listeners interfering with each other.
// Runs ONCE per widget, at page load — it does not display anything itself,
// it only wires up what happens when a star in this specific container is clicked.
// On click: reads the clicked star's data-value, writes it into the linked
// hidden input (this is the actual value sent to the backend), then loops
// over every star in the container and toggles the "filled" class based on
// whether that star's own value is <= the clicked value (so clicking star 3
// fills stars 1-3 and unfills 4-5).

function setupClickableSTars(starContainerSelector, ratingInputId){
    const starContainer = document.querySelector(starContainerSelector);
    const ratingInput = document.getElementById(ratingInputId);

    starContainer.querySelectorAll(".star").forEach(clickedStar => {
        clickedStar.addEventListener("click", () => {
            const value = clickedStar.dataset.value;
            ratingInput.value = value;

            starContainer.querySelectorAll(".star").forEach(starToUpdate => {
                starToUpdate.classList.toggle("filled", starToUpdate.dataset.value <= value);
            });
        });
    });
}


// Sets the visual filled/unfilled state of a star widget to match a given
// rating value, without requiring any click to occur. Called each time the
// edit dialog is OPENED, using the userGameEntry's existing saved rating, so the
// stars correctly reflect "what this userGameEntry is currently rated" the moment
// the dialog appears — independent of the click-handling logic above.
// Does the same <= comparison and "filled" class toggle as the click handler,
// but is triggered by opening the dialog rather than by user interaction.
function showCurrentRating(starContainerSelector, currentRating){
    document.querySelectorAll(`${starContainerSelector} .star`).forEach(starToUpdate => {
        starToUpdate.classList.toggle("filled", currentRating != null && Number(starToUpdate.dataset.value) <= Number(currentRating));
    });
}

setupClickableSTars("#addStarRating", "gameRating"); //add dialogs stars


setupClickableSTars("#editStarRating", "editRating"); //edit dialogs stars
//selects #editStarRating, name of div containing all star spans in edit dialog, and editRating, which is the hidden value applied to the div, storing the int value of edit rating
//"Set up click behavior for the stars inside #editStarRating, and store whichever one gets clicked into #editRating."


//LOGOUT-----------------------------------------------------------------------------------

const logoutbtn = document.getElementById("logoutbtn");
logoutbtn.addEventListener("click", async() => {

    const confirmLogout = confirm("Confirm logout?");
    if(confirmLogout){
        await fetch("/api/users/logout", {
            method: "POST",
            credentials: "include"
        });
        window.location.href = "index.html";
    }

});

//-----------------------------------------------------------------------------------------------

document.addEventListener("DOMContentLoaded", loadCollection); //runs once when the page loads
document.getElementById("collectionView").addEventListener("change", loadCollection); //runs everytime the sort/filter drop down changed