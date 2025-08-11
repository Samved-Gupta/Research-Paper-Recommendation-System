/**
 * Checks if a user is logged in by looking for an authToken.
 * It then shows or hides elements based on their class.
 * - Elements with class "logged-in" are shown only to logged-in users.
 * - Elements with class "guest" are shown only to guests.
 */
function checkAuthState() {
    const authToken = localStorage.getItem('authToken');
    const loggedInElements = document.querySelectorAll('.logged-in');
    const guestElements = document.querySelectorAll('.guest');

    if (authToken) {
        // User is logged in
        loggedInElements.forEach(el => {
            // Use 'flex' for flex items, otherwise use the element's default
            const displayStyle = el.classList.contains('flex') ? 'flex' : 'inline-block';
            el.style.display = displayStyle;
        });
        guestElements.forEach(el => el.style.display = 'none');
    } else {
        // User is a guest
        loggedInElements.forEach(el => el.style.display = 'none');
        guestElements.forEach(el => {
            const displayStyle = el.classList.contains('flex') ? 'flex' : 'inline-block';
            el.style.display = displayStyle;
        });
    }
}

/**
 * Logs the user out by removing the token and redirecting to the login page.
 */
function logout() {
    localStorage.removeItem('authToken');
    window.location.href = 'login.html';
}