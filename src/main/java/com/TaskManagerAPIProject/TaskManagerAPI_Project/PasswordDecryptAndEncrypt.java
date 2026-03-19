package com.TaskManagerAPIProject.TaskManagerAPI_Project;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordDecryptAndEncrypt {

    public String passwordEncoder(String password){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
        return encoder.encode(password);
    }

}
