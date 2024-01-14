package com.taskmanagement.service;

import com.taskmanagement.dto.requestdto.TaskDTO;
import com.taskmanagement.dto.responsedto.TaskResponseDTO;
import com.taskmanagement.enums.TaskPriority;
import com.taskmanagement.enums.TaskStatus;
import com.taskmanagement.exceptions.TaskApiException;
import com.taskmanagement.exceptions.TaskBadRequestException;
import com.taskmanagement.exceptions.UserApiException;
import com.taskmanagement.model.TaskEntity;

import java.util.List;


public interface TaskService {

    TaskResponseDTO createTask(TaskDTO taskDTO, String authorEmail) throws TaskApiException, TaskBadRequestException;

    TaskResponseDTO updateTaskByAuthor(Integer taskId, TaskDTO taskDTO, String updaterEmail) throws TaskApiException, TaskBadRequestException, UserApiException;

    TaskResponseDTO updateTaskByAssignee(Integer taskId, String status, String updaterEmail) throws Exception;

    void deleteTask(Integer taskId, String deleterEmail) throws TaskApiException, TaskBadRequestException;

    TaskEntity getTaskById(Integer taskId) throws TaskApiException, TaskBadRequestException;

    List<TaskResponseDTO> getTasksByAuthor(String authorEmail,String assigneeEmail, String taskStatus, String taskPriority) throws TaskApiException, TaskBadRequestException;

    List<TaskResponseDTO> getTasksByAssignee(String  assigneeEmail,String authorEmail) throws TaskApiException;
}