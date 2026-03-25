package com.TaskManagerAPIProject.TaskManagerAPI_Project.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateTaskRequest {
    private String title;
    private String description;
    private String priority;
    private LocalDate dueDate;
    private String assignedUsername;
}
