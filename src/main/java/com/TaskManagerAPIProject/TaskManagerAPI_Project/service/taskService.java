package com.TaskManagerAPIProject.TaskManagerAPI_Project.service;


import com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository.taskRepo;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class taskService {
    @Autowired
    private taskRepo repo;

    public List<Task> getAllTask(){
        return repo.findAll();
    }

    public Task getTask(int id){
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    public void updateOrInsertTask(Task task) {


        if(task.getId() != null)
        {
            repo.findById(task.getId())
                    .orElseThrow(() -> new RuntimeException("task not found with id" + task.getId()));
        }
         repo.save(task);
    }


    public void deleteTask(int id){
        repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        repo.deleteById(id);

    }
}
