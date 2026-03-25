package com.TaskManagerAPIProject.TaskManagerAPI_Project.service;


import com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository.UserRepo;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository.taskRepo;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.dto.CreateTaskRequest;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.dto.TaskResponse;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.dto.UpdateTaskRequest;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.Task;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.role.Role;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class taskService {

    @Autowired
    private taskRepo taskRepo;

    @Autowired
    private UserRepo userRepo;

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

    public TaskResponse getTask(int id, String username){
        user user = getUserByUsername(username);
        Task task = getTaskById(id);

        if(!canReadTask(user, task)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        return mapToResponse(task);

    }

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


    public void deleteTask(int id, String username){

        user user = getUserByUsername(username);
        Task existingTask = getTaskById(id);

        if(!canModify(user, existingTask)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        taskRepo.deleteById(id);

    }



    private user getUserByUsername(String username){
        user user = userRepo.findByUsername(username.trim().toLowerCase());

        if(user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        return user;
    }

    private Task getTaskById(int id){
        return taskRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    private boolean isAdmin(user user){
        return user.getRole().contains(Role.ADMIN);
    }

    private boolean canReadTask(user user, Task task){
//        if(isAdmin(user)){
//            return true;
//        }
        return task.getCreatedBy().getUsername().equals(user.getUsername())
                || task.getAssignedTo().getUsername().equals(user.getUsername());
    }

    private boolean canModify(user user, Task task){
        if(isAdmin(user)){
            return true;
        }

        return task.getCreatedBy().getUsername().equals(user.getUsername());
    }

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

    private user getAssignedUserForAdmin(String assignedUsername, user user){
        if(assignedUsername == null || assignedUsername.isBlank()){
            return user;
        }

        return getUserByUsername(assignedUsername);
    }
}
