// js/api.js
const BASE_URL = 'https://quiz-platform-api-1sdr.onrender.com'; // Change this if your Spring Boot runs on a different port

const api = {
    request: async (endpoint, method = 'GET', body = null) => {
        const token = localStorage.getItem('jwt_token');

        const headers = {
            'Content-Type': 'application/json'
        };

        // Automatically attach token if the user is logged in
        if (token) {
            headers['Authorization'] = `Bearer ${token}`;
        }

        const config = {
            method,
            headers,
        };

        if (body) {
            config.body = JSON.stringify(body);
        }

        try {
            const response = await fetch(`${BASE_URL}${endpoint}`, config);

            // Handle 401 Unauthorized (Token expired/invalid)
            if (response.status === 401) {
                localStorage.clear();
                window.location.href = '/index.html';
                throw new Error("Session expired. Please log in again.");
            }

            // For endpoints that return empty bodies (like 201 Created or 204 No Content)
            const text = await response.text();
            const data = text ? JSON.parse(text) : {};

            if (!response.ok) {
                throw new Error(data.message || 'Something went wrong');
            }

            return data;
        } catch (error) {
            console.error('API Error:', error);
            throw error;
        }
    }
};