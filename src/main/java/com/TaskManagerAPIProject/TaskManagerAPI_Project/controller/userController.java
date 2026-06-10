package com.TaskManagerAPIProject.TaskManagerAPI_Project.controller;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.user;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class userController {

    @Autowired
    private userService service;


    @PostMapping("/register")
    public ResponseEntity<String> RegisterUser(@RequestBody user user) {
        service.RegisterUser(user);
        return new ResponseEntity<>("Registered", HttpStatus.OK);
    }


}
