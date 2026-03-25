(function () {
    const form = document.getElementById("loginForm");
    const registerForm = document.getElementById("registerForm");
    const messageEl = document.getElementById("loginMessage");
    const registerMessageEl = document.getElementById("registerMessage");
    const usernameEl = document.getElementById("username");
    const passwordEl = document.getElementById("password");
    const registerUsernameEl = document.getElementById("registerUsername");
    const registerPasswordEl = document.getElementById("registerPassword");
    const togglePasswordBtn = document.getElementById("togglePasswordBtn");
    const toggleRegisterPasswordBtn = document.getElementById("toggleRegisterPasswordBtn");

    function setMessage(target, text, isError) {
        target.textContent = text;
        target.className = `inline-message ${isError ? "error" : "success"}`;
    }

    function showMessage(text, isError) {
        setMessage(messageEl, text, isError);
    }

    function showRegisterMessage(text, isError) {
        setMessage(registerMessageEl, text, isError);
    }

    function extractErrorMessage(rawText, fallbackMessage) {
        if (!rawText) {
            return fallbackMessage;
        }

        const normalized = rawText.trim();

        try {
            const parsed = JSON.parse(normalized);
            if (parsed && typeof parsed.message === "string" && parsed.message.trim()) {
                return parsed.message.trim();
            }
            if (parsed && typeof parsed.error === "string" && parsed.error.trim()) {
                return parsed.error.trim();
            }
        } catch (error) {
            // Keep the raw text when the response is not JSON.
        }

        return normalized;
    }

    function mapRegisterErrorMessage(message) {
        const normalized = String(message || "").toLowerCase();

        if (normalized.includes("username already exists")) {
            return "Username already exists. Please choose another one.";
        }

        if (normalized.includes("conflict")) {
            return "Username already exists. Please choose another one.";
        }

        return message || "Registration failed. Please try again.";
    }

    function setButtonLoading(button, isLoading, loadingText) {
        if (isLoading) {
            if (!button.dataset.defaultText) {
                button.dataset.defaultText = button.textContent;
            }
            button.disabled = true;
            button.textContent = loadingText;
            return;
        }

        button.disabled = false;
        button.textContent = button.dataset.defaultText || button.textContent;
    }

    function validateCredentials(username, password) {
        if (!username || !password) {
            throw new Error("Please enter both username and password.");
        }

        if (username.length < 3) {
            throw new Error("Username must be at least 3 characters long.");
        }
    }

    async function tryLogin(username, password) {
        const authHeader = "Basic " + btoa(username + ":" + password);
        const normalizedUsername = username.trim().toLowerCase();
        const response = await fetch("/task", {
            headers: {
                Authorization: authHeader
            }
        });

        if (!response.ok) {
            const detail = await response.text();
            throw new Error(detail || "Login failed. Please check your username and password.");
        }

        sessionStorage.setItem("taskflow.auth", authHeader);
        sessionStorage.setItem("taskflow.user", normalizedUsername);
    }

    async function registerUser(username, password) {
        const response = await fetch("/register", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        const detail = await response.text();

        if (!response.ok) {
            const errorMessage = extractErrorMessage(detail, "Registration failed. Please try a different username.");
            throw new Error(mapRegisterErrorMessage(errorMessage));
        }
    }

    togglePasswordBtn.addEventListener("click", function () {
        const showingPassword = passwordEl.type === "text";
        passwordEl.type = showingPassword ? "password" : "text";
        togglePasswordBtn.textContent = showingPassword ? "Show" : "Hide";
        togglePasswordBtn.setAttribute("aria-label", showingPassword ? "Show password" : "Hide password");
    });

    toggleRegisterPasswordBtn.addEventListener("click", function () {
        const showingPassword = registerPasswordEl.type === "text";
        registerPasswordEl.type = showingPassword ? "password" : "text";
        toggleRegisterPasswordBtn.textContent = showingPassword ? "Show" : "Hide";
        toggleRegisterPasswordBtn.setAttribute("aria-label", showingPassword ? "Show register password" : "Hide register password");
    });

    form.addEventListener("submit", async function (event) {
        event.preventDefault();

        const username = usernameEl.value.trim();
        const password = passwordEl.value;

        const button = document.getElementById("loginBtn");

        try {
            validateCredentials(username, password);
            setButtonLoading(button, true, "Signing in...");
            await tryLogin(username, password);
            showMessage("Login successful. Opening your dashboard...", false);
            window.location.href = "/task-ui.html";
        } catch (error) {
            showMessage(error.message, true);
        } finally {
            setButtonLoading(button, false);
        }
    });

    registerForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const username = registerUsernameEl.value.trim();
        const password = registerPasswordEl.value;
        const button = document.getElementById("registerBtn");

        try {
            validateCredentials(username, password);
            setButtonLoading(button, true, "Registering...");
            await registerUser(username, password);
            showRegisterMessage("User registered successfully. You can sign in now.", false);
            registerForm.reset();
            registerPasswordEl.type = "password";
            toggleRegisterPasswordBtn.textContent = "Show";
            toggleRegisterPasswordBtn.setAttribute("aria-label", "Show register password");
        } catch (error) {
            showRegisterMessage(error.message, true);
        } finally {
            setButtonLoading(button, false);
        }
    });
})();
