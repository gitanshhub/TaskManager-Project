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



    @GetMapping("/task")
    public ResponseEntity<List<TaskResponse>> getAllTask(Principal principal){
        String username = principal.getName();
        List<TaskResponse> tasks = service.getAllTask(username);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/task/{id}")
    public ResponseEntity<TaskResponse> getTask(@PathVariable int id, Principal principal){
        TaskResponse task = service.getTask(id, principal.getName());
        return ResponseEntity.ok(task);
    }

    @PostMapping("/task")
    public ResponseEntity<String> insertTask(@RequestBody CreateTaskRequest task,
                                             Principal principal)
    {

        service.InsertTask(task, principal.getName());
        return new ResponseEntity<>("Inserted", HttpStatus.CREATED);
    }

    @PutMapping("/task/{id}")
    public ResponseEntity<String> updateTask(@PathVariable int id,
                                             @RequestBody UpdateTaskRequest task,
                                             Principal principal)
    {
        service.updateTask(id, task, principal.getName());
        return new ResponseEntity<>("Updated", HttpStatus.OK);
    }

    @DeleteMapping("/task/{id}")
    public ResponseEntity<?> DeleteTask(@PathVariable int id, Principal principal){
        service.deleteTask(id, principal.getName());
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }
}
