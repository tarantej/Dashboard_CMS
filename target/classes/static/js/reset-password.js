document.addEventListener("DOMContentLoaded", function ()
{

    const NewPassword = document.getElementById("NewPassword");
    const confirmPassword = document.getElementById("confirmPassword");

    const strengthLabel = document.getElementById("strengthLabel");

    const bar1 = document.getElementById("bar1");
    const bar2 = document.getElementById("bar2");
    const bar3 = document.getElementById("bar3");

    const resetButton = document.getElementById("resetButton");

    //  Helper function

    function updateRule(icon, valid){

        if(valid){

            icon.className =
                "bi bi-check-circle-fill";

            icon.style.color="#2ecc71";

        }

        else{

            icon.className =
                "bi bi-circle";

            icon.style.color="#bdbdbd";

        }

    }

    function calculateStrength() {

        let score = 0;

        if(NewPassword.value.length >= 8)
            score++;

        if(/[A-Z]/.test(NewPassword.value))
            score++;

        if(/[a-z]/.test(NewPassword.value))
            score++;

        if(/[0-9!@#$%^&*]/.test(NewPassword.value))
            score++;

        //--------------------------------

        bar1.style.background="#d9d9d9";
        bar2.style.background="#d9d9d9";
        bar3.style.background="#d9d9d9";

        //--------------------------------

        if(score <=2){

            strengthLabel.innerText="Weak";
            strengthLabel.style.color="#e63946";

            bar1.style.background="#e63946";
        }

        else if(score==3){

            strengthLabel.innerText="Medium";
            strengthLabel.style.color="#f4a261";

            bar1.style.background="#f4a261";
            bar2.style.background="#f4a261";
        }

        else{

            strengthLabel.innerText="Strong";
            strengthLabel.style.color="#2ecc71";

            bar1.style.background="#2ecc71";
            bar2.style.background="#2ecc71";
            bar3.style.background="#2ecc71";
        }

        const hasLength =
            NewPassword.value.length>=8;

        const hasUpper =
            /[A-Z]/.test(NewPassword.value);

        const hasLower =
            /[a-z]/.test(NewPassword.value);

        const hasNumber =
            /[0-9!@#$%^&*]/.test(NewPassword.value);

        updateRule(lengthRule,hasLength);

        updateRule(upperRule,hasUpper);

        updateRule(lowerRule,hasLower);

        updateRule(numberRule,hasNumber);

        //--------------------------------

        const passwordsMatch =
            NewPassword.value === confirmPassword.value &&
            NewPassword.value.length>0;

        resetButton.disabled = !(score==4 && passwordsMatch);

    }

    NewPassword.addEventListener("input", calculateStrength);

    confirmPassword.addEventListener("input", calculateStrength);

    //  Dynamic Checks

    const lengthRule =
        document.querySelector("#lengthRule i");

    const upperRule =
        document.querySelector("#upperRule i");

    const lowerRule =
        document.querySelector("#lowerRule i");

    const numberRule =
        document.querySelector("#numberRule i");

});

//  Password View Toggle

const toggleIcons =
    document.querySelectorAll(".toggle-password");

toggleIcons.forEach(icon => {

    icon.addEventListener("click", function(){

        const input =
            document.getElementById(this.dataset.target);

        if(input.type==="password"){

            input.type="text";

            this.classList.remove("bi-eye-slash");
            this.classList.add("bi-eye");

        }
        else{

            input.type="password";

            this.classList.remove("bi-eye");
            this.classList.add("bi-eye-slash");

        }

    });

});