// js/student.js
document.addEventListener('DOMContentLoaded', () => {

    const token = localStorage.getItem('jwt_token');
    const role = localStorage.getItem('user_role');
    const name = localStorage.getItem('user_name');
    const email = localStorage.getItem('user_email');

    if (!token || (role !== 'ROLE_STUDENT' && role !== 'STUDENT')) {
        window.location.href = 'index.html';
        return;
    }

    const nameDisplay = document.getElementById('studentNameDisplay');
    const rollDisplay = document.getElementById('studentRollDisplay');

    if (nameDisplay) nameDisplay.innerText = name || "Student";

    // Replaced email with your actual Roll Number
    if (rollDisplay) rollDisplay.innerText = "Roll No: 53";

    const joinForm = document.getElementById('joinSessionForm');
    if (joinForm) {
        joinForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const sessionId = document.getElementById('joinSessionId').value.trim();
            if (sessionId) {
                window.location.href = `quiz-attempt.html?session=${sessionId}`;
            }
        });
    }
});

function logout() {
    localStorage.clear();
    window.location.href = 'index.html';
}