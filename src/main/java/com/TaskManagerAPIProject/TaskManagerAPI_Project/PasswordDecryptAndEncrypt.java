package com.TaskManagerAPIProject.TaskManagerAPI_Project;

import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Component
public class PasswordDecryptAndEncrypt {

    public String passwordEncoder(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        return encoder.encode(password);
    }

}
