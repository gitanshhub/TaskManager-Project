package com.TaskManagerAPIProject.TaskManagerAPI_Project.service;


import com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository.UserRepo;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository.dto.TaskResponse;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository.taskRepo;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.Task;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
public class taskService {

    @Autowired
    private taskRepo taskRepo;

    @Autowired
    private UserRepo userRepo;

    public List<Task> getAllTask(String username){

        return taskRepo.findByAssignedToUsername(username.trim().toLowerCase());

    }

    public Task getTask(int id){
            return taskRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    public void InsertTask(Task task, String username){
        user assignUser = userRepo.findByUsername(username.trim().toLowerCase());

        if(assignUser == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        task.setAssignedTo(assignUser);

        taskRepo.save(task);

    }

    public void updateOrInsertTask(Task task) {

        // This check is for when updating the task.
        if(task.getId() != null)
        {
            taskRepo.findById(task.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"task not found with id" + task.getId()));
        }

         taskRepo.save(task);
    }


    public void deleteTask(int id){
        taskRepo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        taskRepo.deleteById(id);

    }
}
