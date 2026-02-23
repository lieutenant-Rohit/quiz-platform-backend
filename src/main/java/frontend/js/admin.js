// js/admin.js

// ==========================================
// 1. INITIALIZATION & SECURITY
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('jwt_token');
    const role = localStorage.getItem('user_role');
    const email = localStorage.getItem('user_email');

    // Security Check
    if (!token || (role !== 'ROLE_ADMIN' && role !== 'ADMIN')) {
        alert("Unauthorized Access. Please log in as an Admin.");
        window.location.href = 'index.html';
        return;
    }

    if (email) document.getElementById('adminEmailDisplay').innerText = email;

    loadQuizzes();

    // Attach Form Listeners safely
    const newQuizForm = document.getElementById('newQuizForm');
    if (newQuizForm) newQuizForm.addEventListener('submit', handleCreateQuiz);

    const createSessionForm = document.getElementById('createSessionForm');
    if (createSessionForm) createSessionForm.addEventListener('submit', handleCreateSession);

    const addQuestionForm = document.getElementById('addQuestionForm');
    if (addQuestionForm) addQuestionForm.addEventListener('submit', handleAddQuestion);
});

// ==========================================
// 2. NAVIGATION & AUTH LOGIC
// ==========================================
function switchView(viewId) {
    document.querySelectorAll('.view-section').forEach(el => el.classList.add('hidden'));

    const targetView = document.getElementById(viewId);
    if (targetView) targetView.classList.remove('hidden');

    document.querySelectorAll('aside nav button').forEach(btn => {
        btn.classList.remove('bg-indigo-600', 'text-white');
        btn.classList.add('text-gray-300');
    });

    const activeBtnId = viewId === 'quizzesView' ? 'nav-quizzes' :
        viewId === 'sessionsView' ? 'nav-sessions' :
            viewId === 'resultsView' ? 'nav-results' : null;

    if (activeBtnId) {
        const activeBtn = document.getElementById(activeBtnId);
        if (activeBtn) {
            activeBtn.classList.remove('text-gray-300');
            activeBtn.classList.add('bg-indigo-600', 'text-white');
        }
    }
}

function logout() {
    localStorage.clear();
    window.location.href = 'index.html';
}

// ==========================================
// 3. QUIZ MANAGEMENT
// ==========================================
async function loadQuizzes() {
    try {
        const quizzes = await api.request('/admin/quizzes', 'GET');
        const tbody = document.getElementById('quizzesTableBody');
        tbody.innerHTML = '';

        quizzes.forEach(quiz => {
            const statusBadge = quiz.active
                ? `<span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-green-100 text-green-800">Active</span>`
                : `<span class="px-2 inline-flex text-xs leading-5 font-semibold rounded-full bg-red-100 text-red-800">Disabled</span>`;

            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">${quiz.id || 'N/A'}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">${quiz.title}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    ${quiz.topic || 'General'} (${quiz.duration || 0}m)
                </td>
                <td class="px-6 py-4 whitespace-nowrap">${statusBadge}</td>
                <td class="px-6 py-4 whitespace-nowrap text-right text-sm font-medium">
                    <button onclick="toggleQuizStatus('${quiz.id}', ${!quiz.active})" class="text-indigo-600 hover:text-indigo-900 mr-3">Toggle</button>
                    <button onclick="viewQuiz('${quiz.id}')" class="text-blue-600 hover:text-blue-900">Manage</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) {
        console.error("Failed to load quizzes:", error);
    }
}

async function handleCreateQuiz(e) {
    e.preventDefault();

    const title = document.getElementById('quizTitle').value;
    const topic = document.getElementById('quizTopic').value;
    const durationInput = document.getElementById('quizDuration').value;

    if (!title || !topic || !durationInput) {
        alert("Please fill in Title, Topic, and Duration.");
        return;
    }

    try {
        const payload = {
            title: title,
            topic: topic,
            duration: parseInt(durationInput),
            description: topic,
            marksPerQuestion: 1,
            negativeMarks: 0,
            totalMarks: 0
        };

        await api.request('/admin/quizzes', 'POST', payload);
        alert("Quiz Created Successfully!");

        document.getElementById('newQuizForm').reset();
        document.getElementById('createQuizForm').classList.add('hidden');
        loadQuizzes();

    } catch (error) {
        console.error("Quiz Creation Error:", error);
        alert("Failed to create quiz: " + error.message);
    }
}

async function toggleQuizStatus(quizId, newStatus) {
    try {
        await api.request(`/admin/quizzes/${quizId}/status?active=${newStatus}`, 'PUT');
        loadQuizzes(); // Refresh the table
    } catch (error) {
        alert("Failed to update status: " + error.message);
    }
}

// ==========================================
// 4. QUESTION MANAGEMENT
// ==========================================
async function viewQuiz(quizId) {
    try {
        const quiz = await api.request(`/admin/quizzes/${quizId}`, 'GET');

        // Safely update UI headers with fallbacks to prevent undefined errors
        document.getElementById('currentQuizTitle').innerText = quiz.title || 'Untitled Quiz';
        document.getElementById('currentQuizIdDisplay').innerText = quiz.id || quizId;
        document.getElementById('q_quizId').value = quiz.id || quizId;

        renderQuestionsList(quiz.questions || []);
        switchView('manageQuestionsView');
    } catch (error) {
        // Logs the exact failure to your browser's dev tools console
        console.error("View Quiz Error:", error);
        alert("Failed to load quiz details: " + error.message);
    }
}

function renderQuestionsList(questions) {
    const listContainer = document.getElementById('questionsList');
    listContainer.innerHTML = '';

    if (!questions || questions.length === 0) {
        listContainer.innerHTML = `<p class="text-gray-500 text-sm">No questions added yet.</p>`;
        return;
    }

    questions.forEach((q, index) => {
        // FIX: Safe array extraction. If q.options is undefined, it defaults to an empty array.
        const options = q.options || [];
        const correctOptText = options[q.correctAnswerIndex] || 'Unknown Option';

        const div = document.createElement('div');
        div.className = "p-4 border rounded bg-gray-50";
        div.innerHTML = `
            <p class="font-semibold text-gray-800">Q${index + 1}: ${q.questionText || 'Untitled Question'}</p>
            <p class="text-sm text-green-600 mt-1"><i class="bi bi-check-circle"></i> Correct: ${correctOptText}</p>
        `;
        listContainer.appendChild(div);
    });
}

async function handleAddQuestion(e) {
    e.preventDefault();

    const quizId = document.getElementById('q_quizId').value;
    const text = document.getElementById('q_text').value;
    const opt1 = document.getElementById('q_opt1').value;
    const opt2 = document.getElementById('q_opt2').value;
    const opt3 = document.getElementById('q_opt3').value;
    const opt4 = document.getElementById('q_opt4').value;
    // Arrays are 0-indexed, so we subtract 1 from the 1-4 dropdown
    const correctIndex = parseInt(document.getElementById('q_correct').value) - 1;

    try {
        const payload = {
            quizId: quizId,
            questionText: text,
            options: [opt1, opt2, opt3, opt4],
            correctAnswerIndex: correctIndex,
            timeLimitSeconds: 0,
            category: "General"
        };

        await api.request('/admin/quizzes/questions', 'POST', payload);

        alert("Question added successfully!");
        document.getElementById('addQuestionForm').reset();
        document.getElementById('q_quizId').value = quizId; // Restore the hidden ID
        document.getElementById('addQuestionFormContainer').classList.add('hidden');

        // Refresh the list
        viewQuiz(quizId);

    } catch (error) {
        // Log detailed error to console
        console.error("Add Question Error:", error);
        alert("Failed to add question: " + error.message);
    }
}

// ==========================================
// 5. SESSION MANAGEMENT
// ==========================================
async function handleCreateSession(e) {
    e.preventDefault();

    const quizId = document.getElementById('sessionQuizId').value;
    const name = document.getElementById('sessionName').value;
    const start = document.getElementById('sessionStartTime').value;
    const end = document.getElementById('sessionEndTime').value;

    try {
        const payload = {
            quizId: quizId,
            sessionName: name,
            // Convert datetime-local strings to ISO format for Spring Boot Instant
            scheduledStartTime: new Date(start).toISOString(),
            scheduledEndTime: new Date(end).toISOString(),
            allowedStudentIds: [] // Open to all students for now
        };

        const session = await api.request('/admin/sessions', 'POST', payload);

        alert(`Session created successfully!\nSession ID: ${session.id}\nShare this ID with your students.`);
        document.getElementById('createSessionForm').reset();

    } catch (error) {
        alert("Failed to create session: " + error.message);
    }
}

async function changeSessionState(action) {
    const sessionId = document.getElementById('manageSessionId').value;

    if (!sessionId) {
        alert("Please enter a Session ID.");
        return;
    }

    try {
        await api.request(`/admin/sessions/${action}/${sessionId}`, 'POST');
        alert(`Session ${action}ed successfully!`);
    } catch (error) {
        alert(`Failed to ${action} session: ` + error.message);
    }
}

// ==========================================
// 6. RESULTS & LEADERBOARD
// ==========================================
async function fetchResults() {
    const sessionId = document.getElementById('resultSessionId').value;

    if (!sessionId) {
        alert("Please enter a Session ID to fetch results.");
        return;
    }

    try {
        const results = await api.request(`/admin/results/${sessionId}`, 'GET');
        const tbody = document.getElementById('resultsTableBody');
        tbody.innerHTML = '';

        if (results.length === 0) {
            tbody.innerHTML = `<tr><td colspan="3" class="px-6 py-4 text-center text-sm text-gray-500">No submissions found for this session yet.</td></tr>`;
            return;
        }

        results.forEach(res => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">#${res.rank}</td>
                <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                    <div class="font-semibold text-gray-800">${res.studentName}</div>
                    <div class="text-xs text-gray-400">${res.studentEmail}</div>
                </td>
                <td class="px-6 py-4 whitespace-nowrap text-sm font-bold text-indigo-600">${res.score}</td>
            `;
            tbody.appendChild(tr);
        });

    } catch (error) {
        alert("Failed to fetch results: " + error.message);
    }
}