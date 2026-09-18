const registerDialog = document.getElementById("registerDialog")

function openRegister(){
    registerDialog.showModal();
}

function closeRegister(){
    registerDialog.close();
}

const openRegisterbtn = document.getElementById("openRegisterbtn");
openRegisterbtn.addEventListener("click", openRegister);

const cancelRegisterbtn = document.getElementById("cancelRegisterbtn");
cancelRegisterbtn.addEventListener("click", closeRegister);



//REGISTER FORM REQUEST ----------------------------------------------------------------------
const registerForm = document.getElementById("registerForm");
registerForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const username = document.getElementById("usernameNew").value;
    const password = document.getElementById("passwordNew").value;
    const confirmPassword = document.getElementById("passwordNewConfirm").value;

    if(password !== confirmPassword){
        alert("Passwords do not match");
        return;
    }

    const response = await fetch("/api/users", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({username, password})
    });

    if(response.ok){
        closeRegister();
        alert("You are now registered, please login.")
    }else{
        alert("Registration Failed - username may already be taken")
    }
})

//LOGIN FORM ----------------------------------------------------------------------
const loginForm = document.getElementById("loginForm");
loginForm.addEventListener("submit", async (e) => {
    e.preventDefault();

    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;

    const response = await fetch("/api/users/login", {
        method: "POST",
        headers: {"Content-Type": "application/json"},
        body: JSON.stringify({username, password}),
        credentials: "include"
    });

    if(response.ok){
        window.location.href = "collection.html";
    }else{
        alert("Invalid username or password");
    }
})

