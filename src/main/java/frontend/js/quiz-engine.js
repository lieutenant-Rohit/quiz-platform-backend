// js/quiz-engine.js

let currentAttemptId = null;
let questionsList = [];
let currentIndex = 0;

document.addEventListener('DOMContentLoaded', async () => {
    // 1. Get Session ID from URL
    const urlParams = new URLSearchParams(window.location.search);
    const sessionId = urlParams.get('session');

    if (!sessionId) {
        alert("No Session ID provided.");
        window.location.href = 'student-dashboard.html';
        return;
    }

    try {
        // 2. Start the attempt to get the attemptId and quizId
        // Matches StartAttemptResponse.java
        const startResponse = await api.request(`/student/attempts/start/${sessionId}`, 'POST');
        currentAttemptId = startResponse.attemptId;
        const quizId = startResponse.quizId;

        document.getElementById('activeAttemptId').value = currentAttemptId;

        // 3. Fetch the actual quiz details and questions
        // Matches StudentQuizController.java -> GET /student/quizzes/{quizId}
        const quizData = await api.request(`/student/quizzes/${quizId}`, 'GET');

        // Set Title
        document.getElementById('quizTitleDisplay').innerText = quizData.title || "Active Quiz";

        // Load Questions into memory
        questionsList = quizData.questions || [];

        if (questionsList.length === 0) {
            document.getElementById('questionText').innerText = "No questions found for this quiz.";
            return;
        }

        // 4. Start Timer (Defaulting to 30 mins, or quizData.duration if available)
        const duration = quizData.duration ? quizData.duration * 60 : 30 * 60;
        startTimer(duration);

        // 5. Render the first question
        renderQuestion(0);

    } catch (error) {
        alert("Failed to load quiz session: " + error.message);
        window.location.href = 'student-dashboard.html';
    }
});

function renderQuestion(index) {
    if (index < 0 || index >= questionsList.length) return;

    currentIndex = index;
    const question = questionsList[index];

    document.getElementById('currentQuestionId').value = question.id;
    document.getElementById('questionNumberDisplay').innerText = `Question ${index + 1} of ${questionsList.length}`;
    document.getElementById('marksDisplay').innerText = `Marks: ${question.marks || 1}`;
    document.getElementById('questionText').innerText = question.text || question.questionText;

    const optionsContainer = document.getElementById('optionsContainer');
    optionsContainer.innerHTML = '';

    // Map through options and use the INDEX as the value
    (question.options || []).forEach((optText, i) => {
        const optionId = `opt_${i}`;
        const div = document.createElement('div');
        div.className = "flex items-center p-4 border rounded-lg cursor-pointer hover:bg-indigo-50 transition";
        div.onclick = () => document.getElementById(optionId).checked = true;

        div.innerHTML = `
            <input type="radio" id="${optionId}" name="quizOption" value="${i}" class="w-5 h-5 text-indigo-600 focus:ring-indigo-500 border-gray-300">
            <label for="${optionId}" class="ml-3 block text-gray-700 font-medium w-full cursor-pointer">${optText}</label>
        `;
        optionsContainer.appendChild(div);
    });

    // Handle button text (Next vs Finish)
    const nextBtn = document.getElementById('nextBtn');
    if (currentIndex === questionsList.length - 1) {
        nextBtn.innerHTML = `Save & Finish <i class="bi bi-check2-circle ms-2"></i>`;
        nextBtn.classList.replace('bg-indigo-600', 'bg-green-600');
        nextBtn.classList.replace('hover:bg-indigo-700', 'hover:bg-green-700');
    } else {
        nextBtn.innerHTML = `Save & Next <i class="bi bi-arrow-right ms-2"></i>`;
        nextBtn.classList.replace('bg-green-600', 'bg-indigo-600');
        nextBtn.classList.replace('hover:bg-green-700', 'hover:bg-indigo-700');
    }
}

async function saveAndNext() {
    const selectedOption = document.querySelector('input[name="quizOption"]:checked');
    const questionId = document.getElementById('currentQuestionId').value;

    if (!selectedOption) {
        // Optional: Force user to answer, or let them skip
        // alert("Please select an answer.");
        // return;
    }

    if (selectedOption) {
        try {
            // POST /student/attempts/answer
            // Payload strictly matches SubmitAnswerRequest.java
            const payload = {
                attemptId: currentAttemptId,
                questionId: questionId,
                selectedOptionIndex: parseInt(selectedOption.value, 10)
            };

            await api.request('/student/attempts/answer', 'POST', payload);
        } catch (error) {
            console.error("Failed to save answer: ", error);
            // You might want to alert the user here if saving fails
        }
    }

    // Move to next question or submit final
    if (currentIndex < questionsList.length - 1) {
        renderQuestion(currentIndex + 1);
    } else {
        submitFinalQuiz();
    }
}

async function submitFinalQuiz() {
    if (!confirm("Are you sure you want to submit your final attempt?")) return;

    try {
        // POST /student/attempts/submit/{attemptId}
        const score = await api.request(`/student/attempts/submit/${currentAttemptId}`, 'POST');

        alert(`Quiz Submitted Successfully! Your Score: ${score}`);
        window.location.href = 'student-dashboard.html';
    } catch (error) {
        alert("Error submitting quiz: " + error.message);
    }
}

function startTimer(durationInSeconds) {
    let timer = durationInSeconds;
    const display = document.getElementById('timerDisplay');

    const interval = setInterval(() => {
        let minutes = parseInt(timer / 60, 10);
        let seconds = parseInt(timer % 60, 10);

        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;

        display.textContent = minutes + ":" + seconds;

        if (--timer < 0) {
            clearInterval(interval);
            alert("Time's up! Submitting your exam automatically.");
            submitFinalQuiz();
        }
    }, 1000);
}