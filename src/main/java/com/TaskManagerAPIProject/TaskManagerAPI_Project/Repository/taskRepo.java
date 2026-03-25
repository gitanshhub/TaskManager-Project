package com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface taskRepo extends JpaRepository<Task, Integer> {
    List<Task> findByAssignedToUsername(String username);

    List<Task> findByCreatedByUsername(String username);

    List<Task> findByCreatedByUsernameOrAssignedToUsername(String createdByusername, String assignedToUsername);

}
