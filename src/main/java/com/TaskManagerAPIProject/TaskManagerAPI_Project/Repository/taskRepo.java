package com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface taskRepo extends JpaRepository<Task, Integer> {

}
