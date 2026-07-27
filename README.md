# TaskManager

A full-stack task management application built with Spring Boot, PostgreSQL, and a lightweight HTML/CSS/JavaScript dashboard. Users can register, sign in, and manage tasks that they create or are assigned to them.

## Features

- User registration with BCrypt-hashed passwords
- HTTP Basic authentication with stateless security
- Create, view, update, and delete tasks
- Task fields for title, description, priority, and due date
- Ownership-aware access control
  - Users can view tasks they created or that are assigned to them.
  - Users can edit or delete only tasks they created.
  - Admins can edit or delete any task and assign tasks to other users.
- Browser dashboard with task search, statistics, and task forms

## Tech Stack

- Java 21
- Spring Boot 4.0.3
- Spring Web MVC, Spring Data JPA, Spring Security
- PostgreSQL
- Lombok
- HTML, CSS, and vanilla JavaScript

## Prerequisites

- JDK 21
- PostgreSQL
- A database for the application

## Configuration

The application reads its database settings from environment variables:

| Variable | Example |
| --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost:5432/task_manager` |
| `DB_USERNAME` | `postgres` |
| `DB_PASSWORD` | `your-password` |

### Windows PowerShell

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/task_manager"
$env:DB_USERNAME = "postgres"
$env:DB_PASSWORD = "your-password"
```

Hibernate creates or updates the required tables on startup (`spring.jpa.hibernate.ddl-auto=update`).

## Run the Application

From the project root:

```powershell
.\mvnw.cmd spring-boot:run
```

Open [http://localhost:8080](http://localhost:8080) in a browser. Register an account, then sign in to open the dashboard.

To run tests:

```powershell
.\mvnw.cmd test
```

## API

All task endpoints require HTTP Basic authentication. The dashboard handles this automatically after login.

### Register a user

```http
POST /register
Content-Type: application/json
```

```json
{
  "username": "alex",
  "password": "change-me"
}
```

Newly registered users receive the `USER` role.

### Task endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/task` | List tasks visible to the signed-in user |
| `GET` | `/task/{id}` | Get one visible task |
| `POST` | `/task` | Create a task |
| `PUT` | `/task/{id}` | Update a task the user can modify |
| `DELETE` | `/task/{id}` | Delete a task the user can modify |

Example task request:

```json
{
  "title": "Prepare project demo",
  "description": "Finish slides and test the task flow.",
  "priority": "High",
  "dueDate": "2026-08-01",
  "assignedUsername": "alex"
}
```

`assignedUsername` is applied only for admins. A normal user's new tasks are automatically assigned to that user.

### Access rules

- An admin can assign a task to another user.
- A user can see tasks they created and tasks assigned to them.
- A user can update or delete only tasks they created.
- The current project has no task status, completion, or work-update feature. An assigned user cannot mark an admin-created task as completed yet.
- An admin can update or delete any task, but the current list endpoint shows only tasks the admin created or is assigned to.

## Project Structure

```text
src/
|-- main/
|   |-- java/.../
|   |   |-- controller/     # REST endpoints
|   |   |-- service/        # task and user rules
|   |   |-- Repository/     # JPA repositories
|   |   |-- model/          # Task, user, and Role entities
|   |   |-- dto/            # request and response models
|   |   `-- configration/   # Spring Security configuration
|   `-- resources/
|       |-- static/         # login and dashboard UI
|       `-- application.properties
`-- test/
```

## Notes

- The initial public registration flow creates only `USER` accounts. Create an `ADMIN` account separately if you need admin-only assignment and management capabilities.
- Never commit database credentials. Keep them in environment variables as shown above.
