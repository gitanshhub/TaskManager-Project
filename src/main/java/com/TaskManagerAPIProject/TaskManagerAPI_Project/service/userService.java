package com.TaskManagerAPIProject.TaskManagerAPI_Project.service;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository.UserRepo;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.user;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Service
public class userService {

    @Autowired
    private UserRepo repo;

    public List<user> ViewAllUsers() {
        return repo.findAll();

    }

    public void RegisterUser(user user) {
        repo.save(user);
    }



}
