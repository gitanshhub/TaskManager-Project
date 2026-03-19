package com.TaskManagerAPIProject.TaskManagerAPI_Project.service;


import com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository.taskRepo;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.Task;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.dto.response.TaskTitleAndDecsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class taskService {
    @Autowired
    private taskRepo repo;

    public List<Task> getAllTask(){

        return repo.findAll(Sort.by("id").ascending());
    }

    public Task getTask(int id){
            return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    public void updateOrInsertTask(Task task) {

        // This check is for when updating the task.
        if(task.getId() != null)
        {
            repo.findById(task.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"task not found with id" + task.getId()));
        }

         repo.save(task);
    }


    public void deleteTask(int id){
        repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        repo.deleteById(id);

    }
}
