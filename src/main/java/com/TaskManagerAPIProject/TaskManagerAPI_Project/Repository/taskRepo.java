package com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.Task;
import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.dto.response.TaskTitleAndDecsResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface taskRepo extends JpaRepository<Task, Integer> {
    List<TaskTitleAndDecsResponse> findByAssignedToUsername(String username);

}
