package com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponse {

    private String title;
    private String description;
    private String priority;
    private LocalDate dueDate;

}




