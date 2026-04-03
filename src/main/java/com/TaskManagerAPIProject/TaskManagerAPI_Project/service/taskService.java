package com.TaskManagerAPIProject.TaskManagerAPI_Project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository.UserRepo;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository.taskRepo;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.dto.CreateTaskRequest;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.dto.TaskResponse;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.dto.UpdateTaskRequest;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.Task;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.role.Role;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.user;

@Service
public class taskService {

    @Autowired
    private taskRepo taskRepo;

    @Autowired
    private UserRepo userRepo;

    // Returns tasks created by the user or assigned to the user.
    public List<TaskResponse> getAllTask(String username){
        user user = getUserByUsername(username);

        List<Task> task;

            task = taskRepo.findByCreatedByUsernameOrAssignedToUsername(
                    user.getUsername(),
                    user.getUsername()
            );


        return task.stream()
                .map(this::mapToResponse).toList();
    }

    // Loads one task and enforces read access for the requesting user.
    public TaskResponse getTask(int id, String username){
        user user = getUserByUsername(username);
        Task task = getTaskById(id);

        if(!canReadTask(user, task)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return mapToResponse(task);

    }

    // Creates a task and applies assignment rules based on the caller's role.
    public void InsertTask(CreateTaskRequest task, String username){
        user user = getUserByUsername(username);

        Task newTask= new Task();
        newTask.setTitle(task.getTitle());
        newTask.setDescription(task.getDescription());
        newTask.setPriority(task.getPriority());
        newTask.setDueDate(task.getDueDate());
        newTask.setCreatedBy(user);

        if(isAdmin(user)){
            user assignedUser = getAssignedUserForAdmin(task.getAssignedUsername(), user);
            newTask.setAssignedTo(assignedUser);
        }
        else{
            newTask.setAssignedTo(user);
        }

        taskRepo.save(newTask);

    }

    // Updates basic task fields and allows reassignment only for admins.
    public void updateTask(int id, UpdateTaskRequest task, String username) {
        user user = getUserByUsername(username);
        Task existingTask = getTaskById(id);

        if (!canModify(user, existingTask)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);}

        existingTask.setTitle(task.getTitle());
        existingTask.setDescription(task.getDescription());
        existingTask.setPriority(task.getPriority());
        existingTask.setDueDate(task.getDueDate());

        if(isAdmin(user) && task.getAssignedUsername() != null && !task.getAssignedUsername().isBlank()){
            user assignedUser = getUserByUsername(task.getAssignedUsername());
            existingTask.setAssignedTo(assignedUser);
        }

         taskRepo.save(existingTask);
    }


    // Deletes a task after checking write permissions.
    public void deleteTask(int id, String username){

        user user = getUserByUsername(username);
        Task existingTask = getTaskById(id);

        if(!canModify(user, existingTask)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        taskRepo.deleteById(id);

    }


    // Normalizes user lookup through the repository and fails with 404 if missing.
    private user getUserByUsername(String username){
        user user = userRepo.findByUsername(username.trim().toLowerCase());

        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return user;
    }

    // Retrieves a task by id or throws 404 when it does not exist.
    private Task getTaskById(int id){
        return taskRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    // Role helper used by read/write permission checks.
    private boolean isAdmin(user user){
        return user.getRole().contains(Role.ADMIN);
    }

    // A task can be read by its creator or the user it is assigned to.
    private boolean canReadTask(user user, Task task){
//        if(isAdmin(user)){
//            return true;
//        }
        return task.getCreatedBy().getUsername().equals(user.getUsername())
                || task.getAssignedTo().getUsername().equals(user.getUsername());
    }

    // Only admins or the original creator can modify a task.
    private boolean canModify(user user, Task task){
        if(isAdmin(user)){
            return true;
        }

        return task.getCreatedBy().getUsername().equals(user.getUsername());
    }

    // Converts the entity to the API response shape returned by the controller.
    private TaskResponse mapToResponse(Task task){
        return new TaskResponse(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getPriority(),
                task.getDueDate(),
                task.getAssignedTo().getUsername(),
                task.getCreatedBy().getUsername()
        );
    }

    // Admin task creation can target another user; otherwise the admin keeps ownership.
    private user getAssignedUserForAdmin(String assignedUsername, user user){
        if(assignedUsername == null || assignedUsername.isBlank()){
            return user;
        }

        return getUserByUsername(assignedUsername);
    }
}
