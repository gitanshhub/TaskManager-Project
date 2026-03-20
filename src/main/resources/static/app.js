(function () {
    const authHeader = sessionStorage.getItem("taskflow.auth");
    const currentUser = sessionStorage.getItem("taskflow.user");

    if (!authHeader || !currentUser) {
        window.location.href = "/";
        return;
    }

    const currentUserEl = document.getElementById("currentUser");
    const messageEl = document.getElementById("dashboardMessage");
    const resultsEl = document.getElementById("results");
    const taskCountEl = document.getElementById("taskCount");
    const loadTasksBtn = document.getElementById("loadTasksBtn");
    const fetchTaskBtn = document.getElementById("fetchTaskBtn");

    currentUserEl.textContent = currentUser;

    function showMessage(text, isError) {
        messageEl.textContent = text;
        messageEl.className = `inline-message compact-message ${isError ? "error" : "success"}`;
    }

    function setButtonLoading(button, isLoading, loadingText) {
        if (!button) {
            return;
        }

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

    function normalizeText(value) {
        return String(value || "").trim();
    }

    function isPositiveInteger(value) {
        return /^\d+$/.test(String(value)) && Number(value) > 0;
    }

    function isValidDateInput(value) {
        if (!value) {
            return true;
        }

        if (!/^\d{4}-\d{2}-\d{2}$/.test(value)) {
            return false;
        }

        const parsedDate = new Date(`${value}T00:00:00`);
        return !Number.isNaN(parsedDate.getTime()) && parsedDate.toISOString().slice(0, 10) === value;
    }

    function validateTitle(title) {
        if (!title) {
            throw new Error("Title is required.");
        }

        if (title.length < 3) {
            throw new Error("Title must be at least 3 characters long.");
        }

        if (title.length > 100) {
            throw new Error("Title must be 100 characters or fewer.");
        }
    }

    function validateDescription(description) {
        if (description.length > 500) {
            throw new Error("Description must be 500 characters or fewer.");
        }
    }

    function validateDueDate(dueDate) {
        if (!isValidDateInput(dueDate)) {
            throw new Error("Enter a valid due date.");
        }
    }

    function validateTaskId(taskId, actionLabel) {
        if (!isPositiveInteger(taskId)) {
            throw new Error(`Enter a valid task ID to ${actionLabel}.`);
        }
    }

    function validateTaskPayload(payload, requireId) {
        validateTitle(payload.title);
        validateDescription(payload.description);
        validateDueDate(payload.dueDate);

        if (requireId) {
            validateTaskId(payload.id, "update");
        }
    }

    function badgeClass(priority) {
        const value = (priority || "").toLowerCase();
        if (value === "high") return "high";
        if (value === "medium") return "medium";
        if (value === "low") return "low";
        return "none";
    }

    function escapeHtml(value) {
        return String(value ?? "")
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");
    }

    async function apiRequest(url, options) {
        const response = await fetch(url, {
            ...options,
            headers: {
                Authorization: authHeader,
                ...(options && options.body ? { "Content-Type": "application/json" } : {})
            }
        });

        const raw = await response.text();
        let payload = null;

        try {
            payload = raw ? JSON.parse(raw) : null;
        } catch (error) {
            payload = raw;
        }

        if (!response.ok) {
            throw new Error(typeof payload === "string" && payload ? payload : "Request failed.");
        }

        return payload;
    }

    function renderEmpty(message) {
        taskCountEl.textContent = "No tasks loaded yet.";
        resultsEl.innerHTML = `
            <div class="empty-state">
                <div>
                    <h3>No tasks to show</h3>
                    <p>${escapeHtml(message)}</p>
                </div>
            </div>
        `;
    }

    function renderTasks(tasks) {
        if (!Array.isArray(tasks) || tasks.length === 0) {
            renderEmpty("Load tasks from the dashboard to see them here.");
            return;
        }

        taskCountEl.textContent = `${tasks.length} task${tasks.length === 1 ? "" : "s"} loaded`;
        resultsEl.innerHTML = tasks.map(function (task) {
            const title = task.title || "Untitled task";
            const description = task.description || task.Description || "No description provided.";
            const dueDate = task.dueDate || "No due date";
            const priority = task.priority || "Not set";

            return `
                <article class="task-card">
                    <div class="task-card-top">
                        <h3>${escapeHtml(title)}</h3>
                        <span class="badge ${badgeClass(priority)}">${escapeHtml(priority)}</span>
                    </div>
                    <p>${escapeHtml(description)}</p>
                    <div class="task-card-meta">
                        <span class="task-meta-text">Due: ${escapeHtml(dueDate)}</span>
                        <span class="task-meta-text">ID: ${escapeHtml(task.id ?? "-")}</span>
                    </div>
                </article>
            `;
        }).join("");
    }

    function getCreatePayload() {
        const payload = {
            title: normalizeText(document.getElementById("createTitle").value),
            description: normalizeText(document.getElementById("createDescription").value),
            priority: document.getElementById("createPriority").value || null,
            dueDate: document.getElementById("createDueDate").value || null
        };

        validateTaskPayload(payload, false);
        return payload;
    }

    function getUpdatePayload() {
        const taskId = normalizeText(document.getElementById("updateTaskId").value);
        const payload = {
            id: Number(taskId),
            title: normalizeText(document.getElementById("updateTitle").value),
            description: normalizeText(document.getElementById("updateDescription").value),
            priority: document.getElementById("updatePriority").value || null,
            dueDate: document.getElementById("updateDueDate").value || null
        };

        validateTaskId(taskId, "update");
        validateTaskPayload(payload, true);
        return payload;
    }

    function fillUpdateForm(task) {
        document.getElementById("updateTaskId").value = task.id ?? "";
        document.getElementById("updateTitle").value = task.title ?? "";
        document.getElementById("updateDescription").value = task.description ?? task.Description ?? "";
        document.getElementById("updatePriority").value = task.priority ?? "";
        document.getElementById("updateDueDate").value = task.dueDate ?? "";
    }

    async function loadTasks() {
        setButtonLoading(loadTasksBtn, true, "Loading...");

        try {
            const tasks = await apiRequest("/task", {});
            renderTasks(tasks);
            showMessage("Tasks loaded successfully.", false);
        } catch (error) {
            renderEmpty("Unable to load tasks. Please try again.");
            showMessage(error.message, true);
        } finally {
            setButtonLoading(loadTasksBtn, false);
        }
    }

    loadTasksBtn.addEventListener("click", loadTasks);

    document.getElementById("createTaskForm").addEventListener("submit", async function (event) {
        event.preventDefault();
        const submitButton = event.target.querySelector('button[type="submit"]');

        try {
            setButtonLoading(submitButton, true, "Creating...");
            const payload = getCreatePayload();
            await apiRequest("/task", {
                method: "POST",
                body: JSON.stringify(payload)
            });
            showMessage("Task created successfully.", false);
            event.target.reset();
            await loadTasks();
        } catch (error) {
            showMessage(error.message, true);
        } finally {
            setButtonLoading(submitButton, false);
        }
    });

    fetchTaskBtn.addEventListener("click", async function () {
        const taskId = normalizeText(document.getElementById("updateTaskId").value);

        try {
            setButtonLoading(fetchTaskBtn, true, "Loading...");
            validateTaskId(taskId, "fetch");
            const task = await apiRequest(`/task/${taskId}`, {});
            fillUpdateForm(task);
            renderTasks([task]);
            showMessage(`Task ${taskId} loaded into the update section.`, false);
        } catch (error) {
            showMessage(error.message, true);
        } finally {
            setButtonLoading(fetchTaskBtn, false);
        }
    });

    document.getElementById("updateTaskForm").addEventListener("submit", async function (event) {
        event.preventDefault();
        const submitButton = event.target.querySelector('button[type="submit"]');

        try {
            setButtonLoading(submitButton, true, "Updating...");
            const payload = getUpdatePayload();
            await apiRequest("/task", {
                method: "PUT",
                body: JSON.stringify(payload)
            });
            showMessage("Task updated successfully.", false);
            await loadTasks();
        } catch (error) {
            showMessage(error.message, true);
        } finally {
            setButtonLoading(submitButton, false);
        }
    });

    document.getElementById("deleteTaskForm").addEventListener("submit", async function (event) {
        event.preventDefault();
        const submitButton = event.target.querySelector('button[type="submit"]');

        const taskId = normalizeText(document.getElementById("deleteTaskId").value);

        try {
            setButtonLoading(submitButton, true, "Deleting...");
            validateTaskId(taskId, "delete");
            await apiRequest(`/task/${taskId}`, {
                method: "DELETE"
            });
            showMessage(`Task ${taskId} deleted successfully.`, false);
            event.target.reset();
            await loadTasks();
        } catch (error) {
            showMessage(error.message, true);
        } finally {
            setButtonLoading(submitButton, false);
        }
    });

    document.getElementById("logoutBtn").addEventListener("click", function () {
        sessionStorage.removeItem("taskflow.auth");
        sessionStorage.removeItem("taskflow.user");
        window.location.href = "/";
    });

    renderEmpty("Sign in and load tasks to start managing your work.");
    loadTasks();
})();
