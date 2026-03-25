package com.TaskManagerAPIProject.TaskManagerAPI_Project.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateTaskRequest {

    private Integer id;
    private String title;
    private String description;
    private String priority;
    private LocalDate dueDate;
    private String assignedUsername;


}
