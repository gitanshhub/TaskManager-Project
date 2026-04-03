package com.TaskManagerAPIProject.TaskManagerAPI_Project.service;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository.UserRepo;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.role.Role;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class userService {

    @Autowired
    private  UserRepo repo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Registers a new user with a normalized username, encoded password, and default USER role.
    public void RegisterUser(user user) {
        user.setUsername(user.getUsername().trim().toLowerCase());

        if(repo.findByUsername(user.getUsername()) != null)
        {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists. Please choose another one.");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Set.of(Role.USER));

        repo.save(user);
    }


    // Returns all users stored in the database.
    public List<user> getUsers() {
       return repo.findAll();
    }
}
