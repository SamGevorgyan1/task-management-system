package com.taskmanagement.controller;

import com.taskmanagement.dto.requestdto.TaskDTO;
import com.taskmanagement.dto.responsedto.TaskResponseDTO;
import com.taskmanagement.enums.TaskPriority;
import com.taskmanagement.enums.TaskStatus;
import com.taskmanagement.exceptions.TaskApiException;
import com.taskmanagement.exceptions.TaskBadRequestException;
import com.taskmanagement.exceptions.UserApiException;
import com.taskmanagement.model.TaskEntity;
import com.taskmanagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {


    private final TaskService taskService;


    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponseDTO createTask(@RequestBody @Valid TaskDTO taskDTO, Principal principal) throws TaskApiException, TaskBadRequestException {
        return taskService.createTask(taskDTO, principal.getName());
    }


    @PutMapping("/update-by-author/{taskId}")
    public TaskResponseDTO updateTaskByAuthor(
            @PathVariable Integer taskId,
            @RequestBody @Valid TaskDTO taskDTO,
            Principal principal
    ) throws TaskApiException, TaskBadRequestException, UserApiException {
        return taskService.updateTaskByAuthor(taskId, taskDTO, principal.getName());
    }


    @PutMapping("/update-by-assignee/{taskId}")
    public TaskResponseDTO updateTaskByAssignee(
            @PathVariable Integer taskId,
            @RequestParam(required = false) String status,
            Principal principal
    ) throws Exception {
        return taskService.updateTaskByAssignee(taskId, status, principal.getName());
    }


    @GetMapping("/{taskId}")
    public TaskEntity getTaskById(@PathVariable Integer taskId) throws TaskApiException, TaskBadRequestException {
        return taskService.getTaskById(taskId);
    }


    @GetMapping("/assignee/tasks")
    public List<TaskResponseDTO> getAllTasksByAssignee(
            Principal principal,
            @RequestParam(required = false) String authorEmail) throws TaskApiException {
        return taskService.getTasksByAssignee(principal.getName(), authorEmail);
    }


    @GetMapping("/author/tasks")
    public List<TaskResponseDTO> getAllTasksByAuthor(
            Principal principal,
            @RequestParam(required = false) String assigneeEmail,
            @RequestParam(required = false) String taskStatus,
            @RequestParam(required = false) String taskPriority) throws TaskApiException, TaskBadRequestException {
        return taskService.getTasksByAuthor(principal.getName(), assigneeEmail, taskStatus, taskPriority);
    }


    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Integer taskId, Principal principal) throws TaskApiException, TaskBadRequestException {
        taskService.deleteTask(taskId, principal.getName());
    }
}