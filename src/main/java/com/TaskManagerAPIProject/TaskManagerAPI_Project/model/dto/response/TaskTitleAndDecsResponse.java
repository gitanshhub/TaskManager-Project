package com.TaskManagerAPIProject.TaskManagerAPI_Project.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record TaskTitleAndDecsResponse(
        String title,
        String description,
        String priority,
        LocalDate dueDate
) {
}
