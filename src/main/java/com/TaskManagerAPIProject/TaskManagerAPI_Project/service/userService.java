package com.TaskManagerAPIProject.TaskManagerAPI_Project.service;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.PasswordDecryptAndEncrypt;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository.UserRepo;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class userService {

    @Autowired
    private  UserRepo repo;
    @Autowired
    private  PasswordDecryptAndEncrypt passwordDecryptAndEncrypt = new PasswordDecryptAndEncrypt();



    public List<user> ViewAllUsers() {
        return repo.findAll();

    }

    public void RegisterUser(user user) {
        user.setUsername(user.getUsername().trim().toLowerCase());

        if(repo.findByUsername(user.getUsername()) != null)
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists. Please choose another one.");
        }

        user.setPassword(passwordDecryptAndEncrypt.passwordEncoder(user.getPassword()));
        repo.save(user);
    }



}
