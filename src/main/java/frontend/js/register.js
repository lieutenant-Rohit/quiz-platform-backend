// js/register.js

document.addEventListener('DOMContentLoaded', () => {
    const registerForm = document.getElementById('registerForm');
    const errorMessage = document.getElementById('errorMessage');
    const errorText = document.getElementById('errorText');
    const successMessage = document.getElementById('successMessage');
    const registerBtn = document.getElementById('registerBtn');

    if (registerForm) {
        registerForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            // Hide previous messages
            errorMessage.classList.add('hidden');
            successMessage.classList.add('hidden');

            const originalBtnText = registerBtn.innerText;
            registerBtn.innerText = 'Creating account...';
            registerBtn.disabled = true;

            const name = document.getElementById('name').value.trim();
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;
            const role = document.getElementById('role').value;

            // FIX: Convert role to UPPERCASE to match Spring Boot @Pattern validation
            const payload = {
                name: name,
                email: email,
                password: password,
                role: role.toUpperCase() // Sends 'STUDENT' or 'ADMIN' instead of 'student' or 'admin'
            };

            console.log("Sending Registration Payload:", payload); // <-- Debug log

            try {
                // Call the /auth/register endpoint
                await api.request('/auth/register', 'POST', payload);

                // Show success message
                successMessage.classList.remove('hidden');
                registerForm.reset();

                setTimeout(() => {
                    window.location.href = 'index.html';
                }, 2000);

            } catch (error) {
                console.error("Registration Error:", error); // <-- Prints full error to console
                errorText.innerText = error.message || 'Registration failed. Check the console for details.';
                errorMessage.classList.remove('hidden');
            } finally {
                registerBtn.innerText = originalBtnText;
                registerBtn.disabled = false;
            }
        });
    }
});