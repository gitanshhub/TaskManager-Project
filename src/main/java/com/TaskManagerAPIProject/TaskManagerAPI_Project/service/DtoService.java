//package com.TaskManagerAPIProject.TaskManagerAPI_Project.service;
//
//import com.TaskManagerAPIProject.TaskManagerAPI_Project.Repository.taskRepo;
//import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.Task;
//import com.TaskManagerAPIProject.TaskManagerAPI_Project.model.dto.response.TaskTitleAndDecsResponse;
//import jakarta.websocket.server.ServerEndpoint;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//public class DtoService {
//    @Autowired
//    private taskRepo repo;
//
//
//    public List<TaskTitleAndDecsResponse> getAllTask(){
//        List<Task> getTask = repo.findAll();
//
//        List<TaskTitleAndDecsResponse> response = new ArrayList<>();
//
//        for(Task t : getTask){
//            response.add(new TaskTitleAndDecsResponse(t.getTitle(), t.getDescription(), t.getPriority(), t.getDueDate()));
//
//        }
//
//        return response;
//
//    }
//}
