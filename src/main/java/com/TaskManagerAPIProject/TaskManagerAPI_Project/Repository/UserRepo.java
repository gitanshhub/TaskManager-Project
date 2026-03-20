package com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.user;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

@Repository
public interface UserRepo extends JpaRepository<user, Integer> {
    user findByUsername(String username);


}
