document.addEventListener('DOMContentLoaded', async () => {
    const urlParams = new URLSearchParams(window.location.search);
    const sessionId = urlParams.get('session');

    if (!sessionId) {
        alert("No Session ID found!");
        window.location.href = 'student-dashboard.html';
        return;
    }

    try {
        // 1. Start the Attempt (Backend creates the Attempt record)
        const attemptData = await api.request(`/student/attempts/start/${sessionId}`, 'POST');
        const attemptId = attemptData.attemptId;
        const quizId = attemptData.quizId;

        // 2. Fetch Quiz Details (to get duration and questions)
        const quiz = await api.request(`/student/quizzes/${quizId}`, 'GET');

        document.getElementById('quizTitle').innerText = quiz.title;
        document.getElementById('quizTopic').innerText = quiz.topic || "General";

        // 3. Initialize Visual Timer
        startCountdown(quiz.duration, attemptId);

        // 4. Render Questions
        renderQuestions(quiz.questions, attemptId);

    } catch (error) {
        alert("Error: " + error.message);
        window.location.href = 'student-dashboard.html';
    }
});

function startCountdown(minutes, attemptId) {
    let secondsRemaining = minutes * 60;
    const display = document.getElementById('timerDisplay');

    const interval = setInterval(() => {
        const mins = Math.floor(secondsRemaining / 60);
        const secs = secondsRemaining % 60;

        display.innerText = `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;

        if (secondsRemaining <= 0) {
            clearInterval(interval);
            autoSubmit(attemptId);
        }
        secondsRemaining--;
    }, 1000);
}

function renderQuestions(questions, attemptId) {
    const container = document.getElementById('questionsContainer');
    container.innerHTML = '';

    questions.forEach((q, index) => {
        const qDiv = document.createElement('div');
        qDiv.className = "bg-white p-6 rounded-xl shadow-sm border border-gray-200";
        qDiv.innerHTML = `
            <p class="text-lg font-semibold text-gray-800 mb-4">
                <span class="text-indigo-600">Q${index + 1}.</span> ${q.questionText}
            </p>
            <div class="space-y-3">
                ${q.options.map((opt, i) => `
                    <label class="flex items-center p-3 border rounded-lg cursor-pointer hover:bg-gray-50 transition">
                        <input type="radio" name="q_${q.id}" value="${i}" 
                               onchange="saveAnswer('${attemptId}', '${q.id}', ${i})"
                               class="w-4 h-4 text-indigo-600">
                        <span class="ml-3 text-gray-700">${opt}</span>
                    </label>
                `).join('')}
            </div>
        `;
        container.appendChild(qDiv);
    });
}

async function saveAnswer(attemptId, questionId, optionIndex) {
    try {
        // Calls your AttemptService.submitAnswer logic
        await api.request('/student/attempts/answer', 'POST', {
            attemptId: attemptId,
            questionId: questionId,
            selectedOptionIndex: optionIndex
        });
    } catch (error) {
        console.error("Failed to save answer:", error);
    }
}

async function confirmSubmit() {
    if (confirm("Are you sure you want to submit the quiz?")) {
        const urlParams = new URLSearchParams(window.location.search);
        // Logic to trigger final submission...
    }
}