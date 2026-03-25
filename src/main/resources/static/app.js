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
    const taskSearchEl = document.getElementById("taskSearch");
    const statVisibleTasksEl = document.getElementById("statVisibleTasks");
    const statCreatedTasksEl = document.getElementById("statCreatedTasks");
    const statAssignedTasksEl = document.getElementById("statAssignedTasks");
    const statOverdueTasksEl = document.getElementById("statOverdueTasks");
    let loadedTasks = [];

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

        const parts = value.split("-").map(Number);
        const year = parts[0];
        const month = parts[1];
        const day = parts[2];
        const parsedDate = new Date(year, month - 1, day);

        return !Number.isNaN(parsedDate.getTime())
            && parsedDate.getFullYear() === year
            && parsedDate.getMonth() === month - 1
            && parsedDate.getDate() === day;
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
        updateDashboardStats([]);
        resultsEl.innerHTML = `
            <div class="neo-empty-state">
                <div>
                    <h3>No tasks to show</h3>
                    <p>${escapeHtml(message)}</p>
                </div>
            </div>
        `;
    }

    function getLocalToday() {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, "0");
        const day = String(now.getDate()).padStart(2, "0");
        return `${year}-${month}-${day}`;
    }

    function updateDashboardStats(tasks) {
        const visibleTasks = Array.isArray(tasks) ? tasks : [];
        const today = getLocalToday();
        const createdTasks = visibleTasks.filter(function (task) {
            return task.createdByUsername === currentUser;
        }).length;
        const assignedTasks = visibleTasks.filter(function (task) {
            return task.assignedUsername === currentUser;
        }).length;
        const overdueTasks = visibleTasks.filter(function (task) {
            return task.dueDate && task.dueDate < today;
        }).length;

        if (statVisibleTasksEl) statVisibleTasksEl.textContent = String(visibleTasks.length);
        if (statCreatedTasksEl) statCreatedTasksEl.textContent = String(createdTasks);
        if (statAssignedTasksEl) statAssignedTasksEl.textContent = String(assignedTasks);
        if (statOverdueTasksEl) statOverdueTasksEl.textContent = String(overdueTasks);
    }

    function filterTasks(tasks) {
        const query = normalizeText(taskSearchEl ? taskSearchEl.value : "").toLowerCase();
        if (!query) {
            return tasks;
        }

        return tasks.filter(function (task) {
            const searchFields = [
                task.title,
                task.description,
                task.priority,
                task.assignedUsername,
                task.createdByUsername,
                String(task.id ?? "")
            ];

            return searchFields.some(function (value) {
                return String(value || "").toLowerCase().includes(query);
            });
        });
    }

    function renderTasks(tasks) {
        if (!Array.isArray(tasks) || tasks.length === 0) {
            renderEmpty("Load tasks from the dashboard to see them here.");
            return;
        }

        const filteredTasks = filterTasks(tasks);
        updateDashboardStats(tasks);

        if (filteredTasks.length === 0) {
            taskCountEl.textContent = `${tasks.length} task${tasks.length === 1 ? "" : "s"} loaded`;
            resultsEl.innerHTML = `
                <div class="neo-empty-state slim">
                    <div>
                        <h3>No matching tasks</h3>
                        <p>Try a different search term.</p>
                    </div>
                </div>
            `;
            return;
        }

        taskCountEl.textContent = `${filteredTasks.length} of ${tasks.length} task${tasks.length === 1 ? "" : "s"} shown`;
        resultsEl.innerHTML = `
            <div class="neo-table-wrap">
                <table class="neo-table">
                    <thead>
                        <tr>
                            <th>Task</th>
                            <th>Priority</th>
                            <th>Ownership</th>
                            <th>Due Date</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${filteredTasks.map(function (task) {
            const title = task.title || "Untitled task";
            const description = task.description || task.Description || "No description provided.";
            const dueDate = task.dueDate || "No due date";
            const priority = task.priority || "Not set";
            const assignedUsername = task.assignedUsername || "Unassigned";
            const createdByUsername = task.createdByUsername || "Unknown";
            const ownershipLabel = createdByUsername === currentUser
                ? "Created by you"
                : `Created by ${createdByUsername}`;
            const assigneeLabel = assignedUsername === currentUser
                ? "Assigned to you"
                : `Assigned to ${assignedUsername}`;
            const priorityTone = priority.toLowerCase() === "high"
                    ? "hi"
                    : priority.toLowerCase() === "medium"
                        ? "mi"
                        : "li";
            const priorityBadgeClass = badgeClass(priority);

            return `
                            <tr class="neo-table-row ${priorityTone}">
                                <td>
                                    <div class="neo-task-cell">
                                        <p class="neo-task-title">${escapeHtml(title)}</p>
                                        <p class="neo-task-desc">${escapeHtml(description)}</p>
                                        <span class="neo-task-id">Task #${escapeHtml(task.id ?? "-")}</span>
                                    </div>
                                </td>
                                <td class="neo-table-center">
                                    <span class="badge ${priorityBadgeClass}">${escapeHtml(priority)}</span>
                                </td>
                                <td>
                                    <div class="neo-ownership">
                                        <span>${escapeHtml(ownershipLabel)}</span>
                                        <span>${escapeHtml(assigneeLabel)}</span>
                                    </div>
                                </td>
                                <td class="neo-table-center">
                                    <span class="neo-due-chip">${escapeHtml(dueDate)}</span>
                                </td>
                                <td class="neo-table-center">
                                    <div class="neo-row-actions">
                                        <button type="button" class="neo-row-btn" data-action="edit" data-task-id="${escapeHtml(task.id)}">Edit</button>
                                        <button type="button" class="neo-row-btn danger" data-action="delete" data-task-id="${escapeHtml(task.id)}">Delete</button>
                                    </div>
                                </td>
                            </tr>
            `;
                        }).join("")}
                    </tbody>
                </table>
            </div>
        `;
    }

    function getCreatePayload() {
        const payload = {
            title: normalizeText(document.getElementById("createTitle").value),
            description: normalizeText(document.getElementById("createDescription").value),
            priority: document.getElementById("createPriority").value || null,
            dueDate: document.getElementById("createDueDate").value || null,
            assignedUsername: normalizeText(document.getElementById("createAssignedUsername").value) || null
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
            dueDate: document.getElementById("updateDueDate").value || null,
            assignedUsername: normalizeText(document.getElementById("updateAssignedUsername").value) || null
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
        document.getElementById("updateAssignedUsername").value = task.assignedUsername ?? "";
    }

    async function loadTasks() {
        setButtonLoading(loadTasksBtn, true, "Loading...");

        try {
            const tasks = await apiRequest("/task", {});
            loadedTasks = Array.isArray(tasks) ? tasks : [];
            renderTasks(loadedTasks);
            showMessage("Tasks loaded successfully.", false);
        } catch (error) {
            loadedTasks = [];
            renderEmpty("Unable to load tasks. Please try again.");
            showMessage(error.message, true);
        } finally {
            setButtonLoading(loadTasksBtn, false);
        }
    }

    loadTasksBtn.addEventListener("click", loadTasks);
    if (taskSearchEl) {
        taskSearchEl.addEventListener("input", function () {
            renderTasks(loadedTasks);
        });
    }

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
            await apiRequest(`/task/${payload.id}`, {
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

    resultsEl.addEventListener("click", async function (event) {
        const button = event.target.closest("[data-action]");
        if (!button) {
            return;
        }

        const taskId = normalizeText(button.dataset.taskId);
        const action = button.dataset.action;

        if (!taskId) {
            return;
        }

        if (action === "edit") {
            const task = loadedTasks.find(function (item) {
                return String(item.id) === taskId;
            });

            if (!task) {
                showMessage("Unable to find the selected task.", true);
                return;
            }

            fillUpdateForm(task);
            showMessage(`Task ${taskId} loaded into the update section.`, false);
            return;
        }

        if (action === "delete") {
            button.disabled = true;
            const originalText = button.textContent;
            button.textContent = "Deleting...";

            try {
                await apiRequest(`/task/${taskId}`, {
                    method: "DELETE"
                });
                showMessage(`Task ${taskId} deleted successfully.`, false);
                await loadTasks();
            } catch (error) {
                showMessage(error.message, true);
            } finally {
                button.disabled = false;
                button.textContent = originalText;
            }
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
