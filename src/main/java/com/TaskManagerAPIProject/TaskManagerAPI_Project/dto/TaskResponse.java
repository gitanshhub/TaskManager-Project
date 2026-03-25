package com.TaskManagerAPIProject.TaskManagerAPI_Project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private Integer id;
    private String title;
    private String description;
    private String priority;
    private LocalDate dueDate;
    private String assignedUsername;
    private String createdByUsername;
}
