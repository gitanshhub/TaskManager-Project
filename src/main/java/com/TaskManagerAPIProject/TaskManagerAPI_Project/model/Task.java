package com.TaskManagerAPIProject.TaskManagerAPI_Project.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String description;
    private String priority;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dueDate;

    private LocalDate createdAt;

    @ManyToOne
    @JoinColumn(name = "assigned_user_id", nullable = false)
    @JsonIgnore
    private user assignedTo;

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    @JsonIgnore
    private user createdBy;

    @PrePersist
    protected void onCreatedAt(){
        this.createdAt = LocalDate.now();
    }




}
