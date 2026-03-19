package com.TaskManagerAPIProject.TaskManagerAPI_Project.model.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDate;

public record TaskTitleAndDecsResponse(
        String title,
        String Description,
        String priority,
        LocalDate dueDate
) {
}
