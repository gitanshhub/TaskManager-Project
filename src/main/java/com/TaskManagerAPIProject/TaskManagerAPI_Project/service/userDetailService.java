package com.TaskManagerAPIProject.TaskManagerAPI_Project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository.UserRepo;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.UserPrinciple;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.user;

@Service
public class userDetailService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        user user = userRepo.findByUsername(username);

        if(user == null)
            throw new UsernameNotFoundException("NOT FOUND");


        return new UserPrinciple(user);
    }
}
