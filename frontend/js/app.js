// ADD THIS LINE AT THE VERY TOP OF THE FILE
const API_BASE_URL = '%%API_BASE_URL%%';

document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const user = JSON.parse(localStorage.getItem('user'));
    const path = window.location.pathname;

    // Universal header/nav logic
    const dashboardLink = document.getElementById('dashboard-link');
    const searchLink = document.getElementById('search-link');
    const loginRegisterLink = document.getElementById('login-register-link');
    const logoutButton = document.getElementById('logout-button');
    const userProfile = document.getElementById('user-profile');
    const userNameSpan = document.getElementById('user-name');

    if (token && user) {
        // User is logged in
        if (dashboardLink) dashboardLink.style.display = 'block';
        if (searchLink) searchLink.style.display = 'block';
        if (loginRegisterLink) loginRegisterLink.style.display = 'none';
        if (userProfile) {
            userProfile.style.display = 'flex';
            userNameSpan.textContent = user.name || 'User';
        }
    } else {
        // User is not logged in
        if (dashboardLink) dashboardLink.style.display = 'none';
        if (searchLink) searchLink.style.display = 'block'; // Search is always visible
        if (loginRegisterLink) loginRegisterLink.style.display = 'block';
        if (userProfile) userProfile.style.display = 'none';
    }

    if (logoutButton) {
        logoutButton.addEventListener('click', () => {
            localStorage.removeItem('token');
            localStorage.removeItem('user');
            window.location.href = 'login.html';
        });
    }

    // Page-specific logic
    if (path.includes('login.html')) {
        handleLoginPage();
    } else if (path.includes('register.html')) {
        handleRegisterPage();
    } else if (path.includes('search.html')) {
        handleSearchPage(token);
    } else if (path.includes('saved.html')) {
        if (!token) window.location.href = 'login.html';
        else fetchSavedPapers(token);
    } else if (path.includes('history.html')) {
        if (!token) window.location.href = 'login.html';
        else fetchHistory(token);
    } else if (path.includes('account.html')) {
        if (!token) window.location.href = 'login.html';
        else handleAccountPage(user, token);
    } else if (path.includes('forgot-password.html')) {
        handleForgotPasswordPage();
    } else if (path.includes('reset-password.html')) {
        handleResetPasswordPage();
    }
});


// ===================================
// HANDLER FUNCTIONS FOR EACH PAGE
// ===================================

function handleLoginPage() {
    const loginForm = document.getElementById('login-form');
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const messageDiv = document.getElementById('message');

        try {
            const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email, password }),
            });
            const data = await response.json();
            if (response.ok) {
                localStorage.setItem('token', data.token);
                localStorage.setItem('user', JSON.stringify(data.user));
                window.location.href = 'search.html';
            } else {
                messageDiv.textContent = data.message || 'Login failed';
                messageDiv.style.color = 'red';
            }
        } catch (error) {
            messageDiv.textContent = 'An error occurred. Please try again.';
            messageDiv.style.color = 'red';
        }
    });
}

function handleRegisterPage() {
    const registerForm = document.getElementById('register-form');
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const name = document.getElementById('name').value;
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;
        const messageDiv = document.getElementById('message');

        try {
            const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, email, password }),
            });
            const data = await response.json();
            if (response.ok) {
                messageDiv.textContent = 'Registration successful! Please log in.';
                messageDiv.style.color = 'green';
                setTimeout(() => { window.location.href = 'login.html'; }, 2000);
            } else {
                messageDiv.textContent = data.message || 'Registration failed';
                messageDiv.style.color = 'red';
            }
        } catch (error) {
            messageDiv.textContent = 'An error occurred. Please try again.';
            messageDiv.style.color = 'red';
        }
    });
}

function handleSearchPage(token) {
    const searchForm = document.getElementById('search-form');
    searchForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const query = document.getElementById('search-query').value;
        startNewSearch(query, token);
    });
}

function handleAccountPage(user, token) {
    if (user) {
        document.getElementById('profile-name').textContent = user.name;
        document.getElementById('profile-email').textContent = user.email;
    }
    const changePasswordForm = document.getElementById('change-password-form');
    changePasswordForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const currentPassword = document.getElementById('current-password').value;
        const newPassword = document.getElementById('new-password').value;
        const messageDiv = document.getElementById('password-message');

        try {
            const response = await fetch(`${API_BASE_URL}/api/user/change-password`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ currentPassword, newPassword }),
            });
            const data = await response.json();
            if (response.ok) {
                messageDiv.textContent = 'Password changed successfully!';
                messageDiv.style.color = 'green';
                changePasswordForm.reset();
            } else {
                messageDiv.textContent = data.message || 'Failed to change password.';
                messageDiv.style.color = 'red';
            }
        } catch (error) {
            messageDiv.textContent = 'An error occurred.';
            messageDiv.style.color = 'red';
        }
    });
}

function handleForgotPasswordPage() {
    const forgotPasswordForm = document.getElementById('forgot-password-form');
    forgotPasswordForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const email = document.getElementById('email').value;
        const messageDiv = document.getElementById('message');
        try {
            const response = await fetch(`${API_BASE_URL}/api/auth/forgot-password`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email }),
            });
            if (response.ok) {
                messageDiv.textContent = 'If an account with that email exists, a password reset link has been sent.';
                messageDiv.style.color = 'green';
            } else {
                const data = await response.json();
                messageDiv.textContent = data.message || 'An error occurred.';
                messageDiv.style.color = 'red';
            }
        } catch (error) {
            messageDiv.textContent = 'A network error occurred. Please try again.';
            messageDiv.style.color = 'red';
        }
    });
}

function handleResetPasswordPage() {
    const resetPasswordForm = document.getElementById('reset-password-form');
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get('token');

    if (!token) {
        document.getElementById('message').textContent = 'Invalid or missing reset token.';
        return;
    }

    resetPasswordForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const newPassword = document.getElementById('new-password').value;
        const messageDiv = document.getElementById('message');
        try {
            const response = await fetch(`${API_BASE_URL}/api/auth/reset-password`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ token, newPassword }),
            });
            if (response.ok) {
                messageDiv.textContent = 'Password has been reset successfully. You can now log in.';
                messageDiv.style.color = 'green';
                setTimeout(() => { window.location.href = 'login.html'; }, 3000);
            } else {
                const data = await response.json();
                messageDiv.textContent = data.message || 'Failed to reset password. The link may have expired.';
                messageDiv.style.color = 'red';
            }
        } catch (error) {
            messageDiv.textContent = 'A network error occurred. Please try again.';
            messageDiv.style.color = 'red';
        }
    });
}

// ===================================
// REUSABLE API CALLS AND RENDERING
// ===================================

async function startNewSearch(query, token) {
    const resultsDiv = document.getElementById('search-results');
    const messageDiv = document.getElementById('search-message');
    resultsDiv.innerHTML = '<p>Loading...</p>';
    messageDiv.textContent = '';

    try {
        const response = await fetch(`${API_BASE_URL}/api/recommendations/search?q=${encodeURIComponent(query)}`, {
            headers: token ? { 'Authorization': `Bearer ${token}` } : {}
        });
        if (response.ok) {
            const data = await response.json();
            renderSearchResults(data.papers, token);
        } else {
            throw new Error('Search failed');
        }
    } catch (error) {
        resultsDiv.innerHTML = '';
        messageDiv.textContent = 'Search failed. Please try again later.';
        messageDiv.style.color = 'red';
    }
}

async function fetchSavedPapers(token) {
    const resultsDiv = document.getElementById('saved-papers-list');
    const messageDiv = document.getElementById('message');
    resultsDiv.innerHTML = '<p>Loading saved papers...</p>';

    try {
        const response = await fetch(`${API_BASE_URL}/api/bookmarks`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (response.ok) {
            const papers = await response.json();
            renderSavedPapers(papers, token);
        } else {
            messageDiv.textContent = 'Failed to load saved papers.';
        }
    } catch (error) {
        messageDiv.textContent = 'An error occurred while fetching saved papers.';
    }
}

async function fetchHistory(token) {
    const loginHistoryBody = document.getElementById('login-history-body');
    const viewingHistoryBody = document.getElementById('viewing-history-body');

    try {
        // Fetch Login History
        const loginResponse = await fetch(`${API_BASE_URL}/api/history/logins`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (loginResponse.ok) {
            const loginData = await loginResponse.json();
            loginHistoryBody.innerHTML = loginData.map(item => `
                <tr>
                    <td class="px-6 py-4 whitespace-nowrap">${new Date(item.loginTime).toLocaleString()}</td>
                    <td class="px-6 py-4 whitespace-nowrap">${item.ipAddress}</td>
                    <td class="px-6 py-4 whitespace-nowrap">${item.deviceDetails}</td>
                </tr>
            `).join('');
        }

        // Fetch Viewing History
        const viewingResponse = await fetch(`${API_BASE_URL}/api/history/viewing`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (viewingResponse.ok) {
            const viewingData = await viewingResponse.json();
            viewingHistoryBody.innerHTML = viewingData.map(item => `
                <tr>
                    <td class="px-6 py-4 whitespace-nowrap">${new Date(item.viewedAt).toLocaleString()}</td>
                    <td class="px-6 py-4">${item.paper.title}</td>
                </tr>
            `).join('');
        }
    } catch (error) {
        console.error('Failed to fetch history:', error);
    }
}


function renderSearchResults(papers, token) {
    const resultsDiv = document.getElementById('search-results');
    if (!papers || papers.length === 0) {
        resultsDiv.innerHTML = '<p>No papers found for your query.</p>';
        return;
    }
    resultsDiv.innerHTML = papers.map(paper => createPaperCard(paper, token)).join('');
    addCardEventListeners(token);
}

function renderSavedPapers(papers, token) {
    const resultsDiv = document.getElementById('saved-papers-list');
    if (!papers || papers.length === 0) {
        resultsDiv.innerHTML = '<p>You have no saved papers.</p>';
        return;
    }
    resultsDiv.innerHTML = papers.map(paper => createPaperCard(paper.paper, token, true)).join('');
    addCardEventListeners(token);
}

function createPaperCard(paper, token, isSaved = false) {
    // Determine button text and state based on whether the paper is saved
    const buttonText = isSaved ? 'Unsave' : 'Save';
    const buttonClass = isSaved ? 'bg-red-500 hover:bg-red-600' : 'bg-blue-500 hover:bg-blue-600';
    const saveButton = token ? `<button data-paper-id="${paper.id}" class="save-btn px-4 py-2 text-white rounded ${buttonClass}">${buttonText}</button>` : '';

    return `
        <div class="bg-white p-4 rounded-lg shadow-md">
            <h3 class="text-xl font-bold mb-2">${paper.title}</h3>
            <p class="text-gray-600 mb-2"><strong>Authors:</strong> ${paper.authors}</p>
            <p class="text-gray-700 mb-4">${paper.abstractText.substring(0, 250)}...</p>
            <div class="flex justify-between items-center">
                <a href="${paper.url}" target="_blank" class="text-blue-500 hover:underline">Read Paper</a>
                ${saveButton}
            </div>
        </div>
    `;
}

function addCardEventListeners(token) {
    document.querySelectorAll('.save-btn').forEach(button => {
        button.addEventListener('click', async () => {
            if (!token) return;
            const paperId = button.dataset.paperId;
            const isSaved = button.textContent === 'Unsave';

            try {
                const method = isSaved ? 'DELETE' : 'POST';
                const url = `${API_BASE_URL}/api/bookmarks/${paperId}`;
                const response = await fetch(url, {
                    method: method,
                    headers: { 'Authorization': `Bearer ${token}` }
                });

                if (response.ok) {
                    // Toggle button state visually
                    button.textContent = isSaved ? 'Save' : 'Unsave';
                    button.classList.toggle('bg-red-500');
                    button.classList.toggle('hover:bg-red-600');
                    button.classList.toggle('bg-blue-500');
                    button.classList.toggle('hover:bg-blue-600');
                } else {
                    console.error('Failed to update bookmark status');
                }
            } catch (error) {
                console.error('Error updating bookmark:', error);
            }
        });
    });
}