package com.taskmanagement.service.impl;

import com.taskmanagement.dto.requestdto.TaskDTO;
import com.taskmanagement.dto.responsedto.TaskResponseDTO;
import com.taskmanagement.enums.TaskPriority;
import com.taskmanagement.enums.TaskStatus;
import com.taskmanagement.exceptions.*;
import com.taskmanagement.model.TaskEntity;
import com.taskmanagement.model.UserEntity;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.service.TaskService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.taskmanagement.util.EnumUtils.parseEnum;
import static com.taskmanagement.util.converters.TaskDTOConverter.convertTaskEntityToDTO;
import static com.taskmanagement.util.converters.TaskDTOConverter.convertTaskEntityToDTOs;
import static com.taskmanagement.util.messages.CommonErrorMessage.UNAUTHORIZED_OPERATION_MSG;
import static com.taskmanagement.util.messages.TaskErrorMessage.*;
import static com.taskmanagement.util.messages.UserErrorMessage.ERROR_GETTING_USER;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {


    private final TaskRepository taskRepository;
    private final UserRepository userRepository;


    /**
     * Creates a new task based on the provided TaskDTO and the email of the task's author.
     *
     * @param taskDTO     The TaskDTO containing task details.
     * @param authorEmail The email of the task's author.
     * @return TaskResponseDTO representing the created task.
     * @throws TaskApiException If an error occurs during the task creation process.
     */
    @Override
    public TaskResponseDTO createTask(TaskDTO taskDTO, String authorEmail) throws TaskApiException {
        // Retrieve the user (author) based on the provided email.
        UserEntity author = getUserByEmail(authorEmail, ERROR_CREATING_TASK);

        // Initialize a TaskEntity based on the provided TaskDTO and author
        TaskEntity taskEntity = initializeTaskFromDTO(taskDTO, author);

        // Save the task and convert the result to TaskResponseDTO.
        TaskEntity task = saveTask(taskEntity, ERROR_CREATING_TASK);
        return convertTaskEntityToDTO(task);
    }


    /**
     * Updates a task based on the provided TaskDTO and the email of the task's updater (author).
     *
     * @param taskId       The ID of the task to be updated.
     * @param taskDTO      The TaskDTO containing updated task details.
     * @param updaterEmail The email of the task's updater (author).
     * @return TaskResponseDTO representing the updated task.
     * @throws TaskApiException        If an error occurs during the task update process.
     * @throws TaskBadRequestException If the request is malformed.
     * @throws UserNotFoundException   If the user (updater) is not found.
     */
    @Override
    public TaskResponseDTO updateTaskByAuthor(Integer taskId, TaskDTO taskDTO, String updaterEmail) throws TaskApiException, TaskBadRequestException, UserNotFoundException {
        // Retrieve the task and check if the updater is the owner.
        TaskEntity taskEntity = getTaskByIdAndCheckOwnership(taskId, updaterEmail);

        // Update the task details from the provided TaskDTO.
        updateTaskFromDTO(taskEntity, taskDTO);

        // Save the updated task and convert the result to TaskResponseDTO.
        TaskEntity task = saveTask(taskEntity, ERROR_UPDATING_TASK);
        return convertTaskEntityToDTO(task);
    }


    /**
     * Updates a task's status based on the provided status, task ID, and the email of the task's updater (assignee).
     *
     * @param taskId       The ID of the task to be updated.
     * @param status       The new status of the task.
     * @param updaterEmail The email of the task's updater (assignee).
     * @return TaskResponseDTO representing the updated task.
     * @throws TaskApiException        If an error occurs during the task update process.
     * @throws TaskBadRequestException If the request is malformed.
     */
    @Override
    public TaskResponseDTO updateTaskByAssignee(Integer taskId, String status, String updaterEmail) throws Exception, TaskBadRequestException {
        // Parse the task status from the provided string.
        TaskStatus taskStatus;

        validateTaskId(taskId);

        if (status == null || status.isEmpty()) {
            throw new TaskBadRequestException(TASK_STATUS_NULL_OR_EMPTY);
        }
        taskStatus = parseEnum(status, TaskStatus.class, new TaskBadRequestException(INVALID_TASK_STATUS));

        // Retrieve the task and check if the updater is the assignee.
        TaskEntity task = getTaskByIdAndCheckOwnership(taskId, updaterEmail);

        // Update the task status and save the task.
        task.setStatus(taskStatus);
        saveTask(task, ERROR_UPDATING_TASK);

        // Convert the result to TaskResponseDTO.
        return convertTaskEntityToDTO(task);
    }


    /**
     * Deletes a task based on the provided task ID and the email of the task's deleter.
     *
     * @param taskId       The ID of the task to be deleted.
     * @param deleterEmail The email of the task's deleter.
     * @throws TaskApiException        If an error occurs during the task deletion process.
     * @throws TaskBadRequestException If the request is malformed.
     */
    @Override
    public void deleteTask(Integer taskId, String deleterEmail) throws TaskApiException, TaskBadRequestException {

        validateTaskId(taskId);
        // Retrieve the task and check if the deleter has the required permissions.
        TaskEntity task = getTaskByIdAndCheckOwnership(taskId, deleterEmail);

        try {
            // Delete the task.
            taskRepository.delete(task);
        } catch (Exception e) {
            throw new TaskApiException(ERROR_DELETING_TASK);
        }
    }


    /**
     * Retrieves a task based on the provided task ID.
     *
     * @param taskId The ID of the task to be retrieved.
     * @return TaskEntity representing the retrieved task.
     * @throws TaskApiException        If an error occurs during the task retrieval process.
     * @throws TaskBadRequestException If the request is malformed.
     * @throws TaskNotFoundException   If the task is not found with the given ID.
     */
    @Override
    public TaskEntity getTaskById(Integer taskId) throws TaskApiException, TaskBadRequestException {
        // Check if the provided task ID is valid.
        validateTaskId(taskId);

        Optional<TaskEntity> task;
        try {
            // Retrieve the task from the repository based on the provided ID.
            task = taskRepository.findById(taskId);
        } catch (Exception e) {
            throw new TaskApiException(ERROR_GETTING_TASK);
        }

        // Check if the task is present; otherwise, throw an exception.
        if (task.isEmpty()) {
            throw new TaskNotFoundException(TASK_NOT_FOUND);
        }
        return task.get();
    }


    /**
     * Retrieves a list of tasks based on various criteria such as author, assignee, status, and priority.
     *
     * @param authorEmail   The email of the task's author.
     * @param assigneeEmail The email of the task's assignee.
     * @param taskStatus    The status of the tasks to be retrieved.
     * @param taskPriority  The priority of the tasks to be retrieved.
     * @return List of TaskResponseDTO representing the retrieved tasks.
     * @throws TaskApiException        If an error occurs during the task retrieval process.
     * @throws TaskBadRequestException If the request is malformed.
     */
    @Override
    public List<TaskResponseDTO> getTasksByAuthor(String authorEmail, String assigneeEmail, String taskStatus, String taskPriority) throws TaskApiException, TaskBadRequestException {
        List<TaskEntity> taskEntities;
        TaskStatus status = null;
        TaskPriority priority = null;

        // Parse task status and priority if provided.
        if (taskStatus != null) {
            status = parseEnum(taskStatus, TaskStatus.class, new TaskBadRequestException(INVALID_TASK_STATUS));
        }
        if (taskPriority != null) {
            priority = parseEnum(taskPriority, TaskPriority.class, new TaskBadRequestException(INVALID_TASK_PRIORITY));
        }


        try {
            // Retrieve tasks based on the provided criteria.
            if (StringUtils.isNotEmpty(assigneeEmail) && taskStatus != null && taskPriority != null) {
                taskEntities = taskRepository.findByAuthorEmailAndAssigneeEmailAndStatusAndPriority(authorEmail, assigneeEmail, status, priority);
            } else if (StringUtils.isNotEmpty(assigneeEmail) && taskStatus != null) {
                taskEntities = taskRepository.findByAssigneeEmailAndStatus(assigneeEmail, status);
            } else if (StringUtils.isNotEmpty(assigneeEmail) && taskPriority != null) {
                taskEntities = taskRepository.findByAuthorEmailAndAssigneeEmailAndPriority(authorEmail, assigneeEmail, priority);
            } else if (taskStatus != null && taskPriority != null) {
                taskEntities = taskRepository.findByAuthorEmailAndStatusAndPriority(authorEmail, status, priority);
            } else if (StringUtils.isNotEmpty(assigneeEmail)) {
                taskEntities = taskRepository.findByAuthorEmailAndAssigneeEmail(authorEmail, assigneeEmail);
            } else if (taskStatus != null) {
                taskEntities = taskRepository.findByAuthorEmailAndStatus(authorEmail, status);
            } else if (priority != null) {
                taskEntities = taskRepository.findByAuthorEmailAndPriority(authorEmail, priority);
            } else {
                taskEntities = taskRepository.findByAuthorEmail(authorEmail);
            }
        } catch (Exception e) {
            throw new TaskApiException(ERROR_GETTING_TASKS);
        }

        // Convert the retrieved tasks to TaskResponseDTO.
        return convertTaskEntityToDTOs(taskEntities);
    }


    /**
     * Retrieves a list of tasks based on the assignee's email and optionally the author's email.
     *
     * @param assigneeEmail The email of the task's assignee.
     * @param authorEmail   The email of the task's author (optional).
     * @return List of TaskResponseDTO representing the retrieved tasks.
     * @throws TaskApiException If an error occurs during the task retrieval process.
     */
    @Override
    public List<TaskResponseDTO> getTasksByAssignee(String assigneeEmail, String authorEmail) throws TaskApiException {
        List<TaskEntity> taskEntities;
        try {
            // Retrieve tasks based on assignee's email and optionally author's email.
            if (authorEmail != null) {
                taskEntities = taskRepository.findByAuthorEmailAndAssigneeEmail(authorEmail, assigneeEmail);
            } else {
                taskEntities = taskRepository.findByAssigneeEmail(assigneeEmail);
            }
        } catch (Exception e) {
            throw new TaskApiException(ERROR_GETTING_TASKS);
        }

        // Convert the retrieved tasks to TaskResponseDTO.
        return convertTaskEntityToDTOs(taskEntities);
    }


    /**
     * Initializes a new TaskEntity based on the provided TaskDTO and author.
     *
     * @param taskDTO The TaskDTO containing task details.
     * @param author  The UserEntity representing the task's author.
     * @return TaskEntity representing the initialized task.
     * @throws TaskApiException If an error occurs during the initialization process.
     */
    private TaskEntity initializeTaskFromDTO(TaskDTO taskDTO, UserEntity author) throws TaskApiException {
        return TaskEntity.builder()
                .taskId(0)
                .title(taskDTO.getTitle())
                .description(taskDTO.getDescription())
                .status(Enum.valueOf(TaskStatus.class, taskDTO.getTaskStatus()))
                .priority(Enum.valueOf(TaskPriority.class, taskDTO.getTaskPriority()))
                .comments(new ArrayList<>())
                .author(author)
                .assignee(getUserByEmail(taskDTO.getAssigneeEmail(), ASSIGNEE_NOT_FOUND))
                .build();
    }


    /**
     * Updates a task's details based on the provided TaskDTO.
     *
     * @param task    The TaskEntity to be updated.
     * @param taskDTO The TaskDTO containing updated task details.
     * @throws TaskApiException If an error occurs during the update process.
     */
    private void updateTaskFromDTO(TaskEntity task, TaskDTO taskDTO) throws TaskApiException {
        task.setTitle(taskDTO.getTitle());
        task.setDescription(taskDTO.getDescription());
        task.setStatus(Enum.valueOf(TaskStatus.class, taskDTO.getTaskStatus()));
        task.setPriority(Enum.valueOf(TaskPriority.class, taskDTO.getTaskPriority()));
        task.setAssignee(getUserByEmail(taskDTO.getAssigneeEmail(), ASSIGNEE_NOT_FOUND));
    }


    /**
     * Saves a TaskEntity and handles exceptions by converting them to TaskApiException.
     *
     * @param task         The TaskEntity to be saved.
     * @param errorMessage The error message to be used in the exception.
     * @return The saved TaskEntity.
     * @throws TaskApiException If an error occurs during the save process.
     */
    private TaskEntity saveTask(TaskEntity task, String errorMessage) throws TaskApiException {
        try {
            return taskRepository.save(task);
        } catch (Exception e) {
            throw new TaskApiException(errorMessage);
        }
    }


    /**
     * Retrieves a task based on the provided task ID and checks ownership by comparing with the provided user's email.
     *
     * @param taskId    The ID of the task to be retrieved.
     * @param userEmail The email of the user attempting to perform the operation.
     * @return TaskEntity representing the retrieved task.
     * @throws TaskApiException                   If an error occurs during the task retrieval process.
     * @throws TaskBadRequestException            If the request is malformed.
     * @throws TaskUnauthorizedOperationException If the user is not authorized to perform the operation.
     */
    private TaskEntity getTaskByIdAndCheckOwnership(int taskId, String userEmail) throws TaskApiException, TaskBadRequestException {
        // Retrieve the task based on the provided task ID.
        TaskEntity task = getTaskById(taskId);

        // Check if the user has the necessary permissions (author or assignee).
        if (!task.getAuthor().getEmail().equals(userEmail) && !task.getAssignee().getEmail().equals(userEmail)) {
            throw new TaskUnauthorizedOperationException(UNAUTHORIZED_OPERATION_MSG);
        }
        return task;
    }


    /**
     * Retrieves a user entity based on the provided email address.
     *
     * @param email        The email address of the user to be retrieved.
     * @param errorMessage The error message to be thrown if the user is not found.
     * @return The UserEntity corresponding to the provided email.
     * @throws TaskApiException      If an error occurs during the user retrieval process.
     * @throws UserNotFoundException If the user is not found with the provided email.
     */
    private UserEntity getUserByEmail(String email, String errorMessage) throws TaskApiException {
        UserEntity user;
        try {
            // Attempt to retrieve the user from the repository based on the email.
            user = userRepository.findByEmail(email);
        } catch (Exception e) {
            throw new TaskApiException(ERROR_GETTING_USER);
        }

        // If the user is not present in the optional, throw UserNotFoundException.
        if (user == null) {
            throw new UserNotFoundException(errorMessage);
        }

        // Return the retrieved UserEntity.
        return user;
    }


    private void validateTaskId(Integer taskId) throws TaskBadRequestException {
        if (taskId == null) {
            throw new TaskBadRequestException(TASK_ID_NULL);
        }
    }
}