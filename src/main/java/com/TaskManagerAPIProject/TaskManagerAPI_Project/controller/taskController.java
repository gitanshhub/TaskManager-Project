package com.TaskManagerAPIProject.TaskManagerAPI_Project.controller;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.Task;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.dto.response.TaskTitleAndDecsResponse;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.service.DtoService;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.service.taskService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class taskController {
    @Autowired
    private taskService service;

    @Autowired
    private DtoService DtoService;


    @GetMapping("/task")
    public ResponseEntity<List<TaskTitleAndDecsResponse>> getAllTask(@RequestParam String username){
        List<TaskTitleAndDecsResponse> tasks = service.getAllTask(username);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("task/{id}")
    public ResponseEntity<?> getTask(@PathVariable int id){

        Task task = service.getTask(id);
        return ResponseEntity.ok(task);
    }

    @PostMapping("/task")
    public ResponseEntity<String> insertTask(@RequestBody Task task, @RequestParam String username ){
        service.InsertTask(task, username);
        return new ResponseEntity<>("Inserted", HttpStatus.CREATED);
    }

    @PutMapping("task")
    public ResponseEntity<String> updateTask(@RequestBody Task task){
        service.updateOrInsertTask(task);
        return new ResponseEntity<>("Updated", HttpStatus.OK);
    }

    @DeleteMapping("task/{id}")
    public ResponseEntity<?> DeleteTask(@PathVariable int id){
        service.deleteTask(id);
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }
}
