package com.TaskManagerAPIProject.TaskManagerAPI_Project.controller;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.dto.TaskResponse;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.dto.CreateTaskRequest;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.dto.UpdateTaskRequest;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.Task;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.service.taskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class taskController {
    @Autowired
    private taskService service;


    // Returns tasks visible to the authenticated user.
    @GetMapping("/task")
    public ResponseEntity<List<TaskResponse>> getAllTask(Principal principal){
        String username = principal.getName();
        List<TaskResponse> tasks = service.getAllTask(username);
        return ResponseEntity.ok(tasks);
    }

    // Returns one task only if the authenticated user is allowed to view it.
    @GetMapping("/task/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable int id, Principal principal){
        TaskResponse task = service.getTask(id, principal.getName());
        return ResponseEntity.ok(task);
    }

    // Creates a new task for the authenticated user.
    // Admins can assign it to another user; regular users are assigned to themselves.
    @PostMapping("/task")
    public ResponseEntity<String> insertTask(@RequestBody CreateTaskRequest task,
                                             Principal principal)
    {

        service.InsertTask(task, principal.getName());
        return new ResponseEntity<>("Inserted", HttpStatus.CREATED);
    }

    // Updates an existing task if the authenticated user has permission to modify it.
    @PutMapping("/task/{id}")
    public ResponseEntity<String> updateTask(@PathVariable int id,
                                             @RequestBody UpdateTaskRequest task,
                                             Principal principal)
    {
        service.updateTask(id, task, principal.getName());
        return new ResponseEntity<>("Updated", HttpStatus.OK);
    }

    // Deletes a task if the authenticated user is allowed to modify it.
    @DeleteMapping("/task/{id}")
    public ResponseEntity<?> DeleteTask(@PathVariable int id, Principal principal){
        service.deleteTask(id, principal.getName());
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }
}
