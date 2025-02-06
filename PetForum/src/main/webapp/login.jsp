<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Login Page</title>
	<style>
        body
        {
            display: grid;
            grid-template-columns: 2fr 0.7fr;
        }
        .formContainer
        {
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        #loginForm, #registerForm
        {
            max-width: 400px;
            border-radius: 10px;
            text-align: center;
            display: flex;
            justify-content: center;
        }
        #loginForm h2, #registerForm h2
        {
            color: #23363D;
            font-size: 2.5rem;
            margin-bottom: 20px;
            font-weight: 600;
        }
        input 
        {
            box-sizing: border-box;
            padding: 12px;
            margin: 5px 0;
            border-radius: 5px;
            border: none;
            outline: none;
            font-size: 1rem;
        }
        input[type=text], input[type=password]
        {
            background-color: #9FA4BF;
        }
        input[type=submit]
        {
            background-color: #1E4210;
            color: white;
            font-size: 1.2rem;
        }
        button 
        {
            padding: 12px;
            margin: 10px 0;
            border-radius: 5px;
            border: none;
            outline: none;
            font-size: 1rem;
            background-color: #4E7CD9;
            color: white;
        }
        #loginForm p, #registerForm p
        {
            margin: 0;
            display: inline-block;
            margin-right: 20px;
            font-size: 1.2rem;
        }
        .aboutusContainer
        {
            /* background-color: #F2C094; */
            background-image: url("images/leaves1.jpg");
            background-repeat: no-repeat;
            background-size: cover;
            display: flex;
            justify-content: center;
            align-items: center;
            flex-direction: column;
        }
        .aboutusContainer h1
        {
            background-color: #FBDAB9;
            font-size: 3rem;
            font-family: "Comic Sans MS", "Comic Sans", cursive;
            color: #1E4210; 
        }
        .aboutUs p
        {
            font-size: 1.2rem;
            text-align: justify;
        }
        .aboutUs
        {
            width: 70%;
            padding: 50px;
            background-color: #1F4222;
            color: white;
            text-align: center;
        }
        .slideshowContainer
        {
            position: relative;
            height: 300px;
        }
        .slideshowContainer img
        {
            width: 100%;
            height: 300px;
            object-fit: cover;
            border-radius: 10px;
        }
        .prev, .next {
            position: absolute;
            top: 50%;
            background-color: rgba(0,0,0,0.5);
            z-index: 1;
            padding: 10px;
            color: white;
            font-weight: 800;
        }
        .prev
        {
            left: 0;
        }
        .next {
            right: 0;
            border-radius: 3px 0 0 3px;
        }
	</style>
    <script>
        // display login form
        function showLoginForm() {
            document.getElementById('registerForm').style.display = 'none';
            document.getElementById('loginForm').style.display = 'block';
        }

        // difplay register form
        function showRegisterForm() {
            document.getElementById('loginForm').style.display = 'none';
            document.getElementById('registerForm').style.display = 'block';
        }

        // validate register
        function validateRegister() {
            // get fields
            const username = document.getElementById("username").value;
            const password = document.getElementById("password").value;
            const confirmPassword = document.getElementById("confirmPassword").value;

            // check empty
            if (username === "" || password === "" || confirmPassword === "") {
                alert("Username or password cannot be empty");
                return false;
            }

            // check password consistency
            if (password !== confirmPassword) {
                alert("Inconsistent passwords");
                return false;
            }
            // check illegal characters
            const illegalChars = /[^a-zA-Z0-9]/;
            if (illegalChars.test(username) || illegalChars.test(password)) {
                alert("Username or password can only be combination of numbers and letters");
                return false;
            }

            // add addtional validation here


            // validate success and send
            postRegister(username, password);
        }

        // send register data
        function postRegister(username, password) {
            fetch('register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    'username': username,
                    'password': password
                })
            }).then(response => {
                if (response.ok) { // sent back success response 
                    return response.text();
                } else if (response.status === 409) { // unsuccessful response, defined in backend
                    throw new Error("Username already exists.");
                } else {
                    throw new Error("Registration failed.");
                }
            }).then(data => {
                window.location.href = "forum"; // Redirect to forum or login page
            }).catch(error => {
                alert(error.message); // Show error message to user
            });
        }
        
        function postLogin() {
            const username = document.getElementById("usernameLogin").value;
            const password = document.getElementById("passwordLogin").value;
            
            fetch('login', {
                method: 'POST',
                headers: {
                	'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: new URLSearchParams({
                    'username': username,
                    'password': password
                })
            }).then(response => {
                if (response.ok) {
                    return response.json();
                } else if (response.status === 401) {
                    throw new Error("Invalid username or password.");
                } else {
                    throw new Error("Login failed.");
                }
            }).then(data => {
                // alert("data:" + data);
                // alert("data.userId:" + data.userId);
                sessionStorage.setItem("userId", data.userId);
            	window.location.href = "forum";
            }).catch(error => {
                alert(error.message);
            });
        }

        //Function for slideshow
        let slideIndex = 0;
        showSlides(slideIndex);

        function plusSlides(n) {
            showSlides(slideIndex += n);
        }

        function showSlides(n) {
            let i;
            let slides = document.getElementsByClassName("slides");
            if (n >= slides.length) { slideIndex = 0 }
            if (n < 0) { slideIndex = slides.length - 1 }
            for (i = 0; i < slides.length; i++) {
                slides[i].style.display = "none";
            }
            slides[slideIndex].style.display = "inline-block";
        }
    </script>
</head>
<body>
    <div class="aboutusContainer">
        <div class="aboutUs">
            <h1>One With Nature</h1>
            <h2>About Us</h2>
            <p>
                We are a community dedicated to fostering a harmonious relationship between people, wildlife, and stray animals. Inspired by the rich biodiversity around us, including endangered species like the Sunda pangolin and the oriental pied hornbill, we aim to provide a platform for wildlife enthusiasts and animal lovers to come together for a common cause.
                Whether you're seeking help, sharing experiences, or posting adoption and foster notices for stray animals, we encourage responsible engagement with animals and nature. Join us in promoting conservation, protecting local wildlife, and creating a compassionate space for all creatures to thrive.
            </p>
            <div class="slideshowContainer">
                <img class="slides fade" src="images/yunnan.jpg" alt="NTU Wildlife 1">
                <img class="slides fade" src="images/otters.jpg" alt="NTU Wildlife 2" style="display:none;">
                <img class="slides fade" src="images/bird.jpg" alt="NTU Wildlife 3" style="display:none;">
                <a class="prev" onclick="plusSlides(-1)">&#10094;</a>
                <a class="next" onclick="plusSlides(1)">&#10095;</a>
            </div>
        </div>
    </div>
    <div class="formContainer">
        <!-- login form -->
        <div id="loginForm" style="display: block;">
            <h2>Welcome back!</h2>
            <form onsubmit="event.preventDefault(); postLogin();">
                <input type="text" id="usernameLogin" name="username" size="25" placeholder="Username" required><br>
                <input type="password" id="passwordLogin" name="password" size="25" placeholder="Password" required><br><br>
                <input type="submit" value="Log in">
            </form>
            <p>Not a member yet? </p><button onclick="showRegisterForm()">Register</button>
        </div>

        <!-- register form-->
        <div id="registerForm" style="display: none;">
            <h2>Create Account</h2>
            <form onsubmit="event.preventDefault(); validateRegister();">
                <input type="text" id="username" name="username" size="25" placeholder="Username"><br>
                <input type="password" id="password" name="password" size="25" placeholder="Password"><br>
                <input type="password" id="confirmPassword" name="confirmPassword" size="25" placeholder="Confirm Password"><br><br>
                <input type="submit" value="Register">
            </form>
            <button onclick="showLoginForm()">Back to Login</button>
        </div>
    </div>
</body>
</html>	