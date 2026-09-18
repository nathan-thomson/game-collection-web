//LOAD USERS COLLECTION--------------------------------------------------------------------------------------------
async function loadCollection(){ //fetch is set to GET default, only changes if told
    const response = await fetch("/api/usergames/user/collection", {
        credentials: "include"
    });

    if(!response.ok){
        window.location.href = "index.html";
        return;
    }

    const collection = await response.json();
    const container = document.getElementById("collectionContainer");
    container.innerHTML = "";

    collection.forEach(entry => createCard(entry));

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


//BUILD A CARD FROM A USERGAME ENTRY ---------------------------------------------------------------------

function createCard(entry){

    const container = document.getElementById("collectionContainer");

    const card = document.createElement("div");
    card.className = "gameCard";
    card.dataset.id = entry.id;

    card.innerHTML = `
            <div class="cardImage">
                <img src="${entry.game.coverURL}">
            </div>
            <h3 class="cardTitle">${entry.game.title}</h3>
            <p class="cardStatus">${entry.status}</p>
            <div class="cardRating">${renderStars(entry.rating)}</div>
            <p class="cardReview">${entry.review ?? "No review yet"}</p>
            <button class="deletebtn">Delete</button>
        `;

    const deletebtn = card.querySelector(".deletebtn");
    deletebtn.addEventListener("click", async () => {

        const confirmDelete = confirm("Are you sure you want to delete?");
        if(confirmDelete){
            const response = await fetch(`/api/usergames/${entry.id}`, {
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
    img.src = entry.game.coverURL ?? "placeholder.png"; //set src after attaching listener

    container.appendChild(card);
}


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
        const newEntry = await response.json();
        createCard(newEntry);
        closeGameDialogue();
        addGameForm.reset();
    }else{
        alert("Failed to add game");
    }
})


//STAR RATING SYSTEM ---------------------------------------------------------------------------
//Found pieces online, attach a click listener to every star at once
//(".star), finds all elements in class star
//value becomes the number assigned to the selected star
//value is then set to hidden input 'gameRating'
document.querySelectorAll(".star").forEach(star =>{
    star.addEventListener("click", () => {

        const value = star.dataset.value;
        document.getElementById("gameRating").value = value;

        document.querySelectorAll(".star").forEach(s => {
            s.classList.toggle("filled", s.dataset.value <= value);
        }); //loops through each star, comparing its value to chosen rating, if rating is greater than star, fill in star
    });
});

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

document.addEventListener("DOMContentLoaded", loadCollection);