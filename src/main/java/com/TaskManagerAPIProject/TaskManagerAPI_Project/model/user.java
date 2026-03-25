package com.TaskManagerAPIProject.TaskManagerAPI_Project.model;

import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class user {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true, nullable = false)
    private String username;
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> role = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "assignedTo")
    private List<Task> assignedTasks;

    @JsonIgnore
    @OneToMany(mappedBy = "createdBy")
    private List<Task> createdTask;



}
