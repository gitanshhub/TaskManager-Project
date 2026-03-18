package com.TaskManagerAPIProject.TaskManagerAPI_Project;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.user;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordDecryptAndEncrypt {

    public String passwordEncoder(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        return encoder.encode(password);
    }

}
