package com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.user;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepo extends JpaRepository<user, Integer> {
    user findByUsername(String username);


}
