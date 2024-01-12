package com.taskmanagement.repository;

import com.taskmanagement.enums.TaskPriority;
import com.taskmanagement.enums.TaskStatus;
import com.taskmanagement.model.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Integer> {


    List<TaskEntity> findByAuthorEmail(String authorEmail);

    List<TaskEntity> findByAuthorEmailAndStatus(String authorEmail, TaskStatus taskStatus);

    List<TaskEntity> findByAuthorEmailAndPriority(String authorEmail, TaskPriority taskPriority);


    List<TaskEntity> findByAssigneeEmailAndStatus(String authorEmail, TaskStatus taskStatus);

    List<TaskEntity> findByAuthorEmailAndStatusAndPriority(String authorEmail, TaskStatus taskStatus, TaskPriority taskPriority);

    List<TaskEntity> findByAuthorEmailAndAssigneeEmailAndPriority(String authorEmail, String assigneeEmail, TaskPriority taskPriority);

    List<TaskEntity> findByAuthorEmailAndAssigneeEmailAndStatusAndPriority(String authorEmail, String assigneeEmail, TaskStatus taskStatus, TaskPriority taskPriority);


    List<TaskEntity> findByAssigneeEmail(String assigneeEmail);

    List<TaskEntity> findByAuthorEmailAndAssigneeEmail(String authorEmail, String assigneeEmail);

}