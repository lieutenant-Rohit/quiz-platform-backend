// js/auth.js
document.addEventListener('DOMContentLoaded', () => {
    const loginForm = document.getElementById('loginForm');
    const errorMessage = document.getElementById('errorMessage');
    const errorText = document.getElementById('errorText');
    const loginBtn = document.getElementById('loginBtn');

    if (loginForm) {
        loginForm.addEventListener('submit', async (e) => {
            e.preventDefault();

            errorMessage.classList.add('hidden');

            const originalBtnText = loginBtn.innerText;
            loginBtn.innerText = 'Signing in...';
            loginBtn.disabled = true;

            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            try {
                const response = await api.request('/auth/login', 'POST', {
                    email: email,
                    password: password
                });

                // ==========================================
                // STORAGE UPDATED
                // ==========================================
                localStorage.setItem('jwt_token', response.token);
                localStorage.setItem('user_role', response.role);
                localStorage.setItem('user_email', email);

                // We store the name returned by the backend to display in dashboards
                // Ensure your LoginResponse.java includes a 'name' field
                localStorage.setItem('user_name', response.name || 'User');

                // Route based on role
                if (response.role === 'ROLE_ADMIN' || response.role === 'ADMIN') {
                    window.location.href = 'admin-dashboard.html';
                } else {
                    window.location.href = 'student-dashboard.html';
                }

            } catch (error) {
                errorText.innerText = error.message || 'Invalid email or password.';
                errorMessage.classList.remove('hidden');
            } finally {
                loginBtn.innerText = originalBtnText;
                loginBtn.disabled = false;
            }
        });
    }
});