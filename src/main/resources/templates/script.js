
    function validateEmail() {
        var email = document.getElementById("email").value;
        var emailError = document.getElementById("emailError");
        var submitButton = document.getElementById("submitButton");

        var emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(email)) {
            emailError.textContent = "Invalid email address!";
            document.getElementById("email").classList.add("input-error");
            submitButton.disabled = true;
        } else {
            emailError.textContent = "";
            document.getElementById("email").classList.remove("input-error");
            submitButton.disabled = false;
        }
    }

    function checkPasswordRequirements() {
        var password = document.getElementById("password").value;
        var passwordRequirements = document.getElementById("passwordRequirements");
        var submitButton = document.getElementById("submitButton");

        var requirementsMet = true;

        // Check password length
        if (password.length < 8) {
            requirementsMet = false;
            passwordRequirements.innerHTML = "✕ At least 8 characters<br>";
        } else {
            passwordRequirements.innerHTML = "✓ At least 8 characters<br>";
        }

        // Check for uppercase letter
        if (!/[A-Z]/.test(password)) {
            requirementsMet = false;
            passwordRequirements.innerHTML += "✕ At least 1 uppercase letter<br>";
        } else {
            passwordRequirements.innerHTML += "✓ At least 1 uppercase letter<br>";
        }

        // Check for lowercase letter
        if (!/[a-z]/.test(password)) {
            requirementsMet = false;
            passwordRequirements.innerHTML += "✕ At least 1 lowercase letter<br>";
        } else {
            passwordRequirements.innerHTML += "✓ At least 1 lowercase letter<br>";
        }

        // Check for digit
        if (!/[0-9]/.test(password)) {
            requirementsMet = false;
            passwordRequirements.innerHTML += "✕ At least 1 digit";
        } else {
            passwordRequirements.innerHTML += "✓ At least 1 digit";
        }

        if (requirementsMet) {
            passwordRequirements.classList.remove("text-danger");
            passwordRequirements.classList.add("text-success");
            document.getElementById("confirmPassword").disabled = false;
        } else {
            passwordRequirements.classList.remove("text-success");
            passwordRequirements.classList.add("text-danger");
            document.getElementById("confirmPassword").disabled = true;
        }

        submitButton.disabled = true;
    }
document.getElementById("password").addEventListener("input", function() {
    checkPasswordRequirements();
});

//    function checkPasswordMatch(confirmPassword) {
//        var password = document.getElementById("password").value;
//        var passwordMatchError = document.getElementById("passwordMatchError");
//        var submitButton = document.getElementById("submitButton");
//
//        if (password === confirmPassword) {
//            passwordMatchError.textContent = "";
//            submitButton.disabled = false;
//        } else {
//            passwordMatchError.textContent = "Passwords do not match!";
//            submitButton.disabled = true;
//        }
//    }

