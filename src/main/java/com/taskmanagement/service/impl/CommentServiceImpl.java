package com.taskmanagement.service.impl;

import com.taskmanagement.dto.requestdto.CommentDTO;
import com.taskmanagement.dto.responsedto.CommentResponseDTO;
import com.taskmanagement.exceptions.*;
import com.taskmanagement.model.CommentEntity;
import com.taskmanagement.model.TaskEntity;
import com.taskmanagement.model.UserEntity;
import com.taskmanagement.repository.CommentRepository;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.taskmanagement.util.converters.CommentDTOConverter.convertCommentEntitiesToDTOS;
import static com.taskmanagement.util.converters.CommentDTOConverter.convertCommentEntityToDTO;
import static com.taskmanagement.util.messages.CommentErrorMessage.*;
import static com.taskmanagement.util.messages.CommonErrorMessage.UNAUTHORIZED_OPERATION_MSG;
import static com.taskmanagement.util.messages.TaskErrorMessage.TASK_NOT_FOUND;


@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;


    /**
     * Creates a new comment for the specified task and user.
     *
     * @param taskId     The ID of the task for which the comment is created.
     * @param userEmail  The email of the user creating the comment.
     * @param commentDTO The DTO containing the comment information.
     * @return CommentResponseDTO representing the created comment.
     * @throws CommentApiException                   If an error occurs during comment creation.
     * @throws CommentBadRequestException            If the request is malformed.
     * @throws CommentUnauthorizedOperationException If the user is not authorized to perform the operation.
     */
    @Override
    public CommentResponseDTO createComment(Integer taskId, String userEmail, CommentDTO commentDTO) throws CommentApiException {

        if (taskId == null) {
            throw new CommentBadRequestException("Task Id can not be null");
        }
        Optional<TaskEntity> task = taskRepository.findById(taskId);

        if (task.isEmpty()) {
            throw new TaskNotFoundException(TASK_NOT_FOUND);
        }
        if (!checkCommentPermission(task.get(), userEmail)) {
            throw new CommentUnauthorizedOperationException(UNAUTHORIZED_OPERATION_MSG);
        }

        UserEntity user = userRepository.findByEmail(userEmail);

        CommentEntity commentEntity = new CommentEntity();
        commentEntity.setCommentId(0);
        commentEntity.setContent(commentDTO.getText());
        commentEntity.setUser(user);
        commentEntity.setTask(task.get());

        return convertCommentEntityToDTO(saveComment(commentEntity));
    }


    /**
     * Deletes the comment with the specified ID, performed by the user with the given email.
     *
     * @param commentId The ID of the comment to be deleted.
     * @param userEmail The email of the user attempting to delete the comment.
     * @throws CommentApiException                   If an error occurs during comment deletion.
     * @throws CommentUnauthorizedOperationException If the user is not authorized to perform the operation.
     */
    @Override
    public void deleteComment(Integer commentId, String userEmail) throws CommentApiException {
        CommentEntity comment = getCommentById(commentId);

        if (!comment.getUser().getEmail().equals(userEmail)) {
            throw new CommentUnauthorizedOperationException(UNAUTHORIZED_OPERATION_MSG);
        }
        try {
            commentRepository.delete(comment);
        } catch (Exception e) {
            throw new CommentApiException(ERROR_DELETING_COMMENT);
        }
    }


    /**
     * Retrieves a comment based on the provided comment ID.
     *
     * @param commentId The ID of the comment to be retrieved.
     * @return CommentEntity representing the retrieved comment.
     * @throws CommentApiException    If an error occurs during comment retrieval.
     * @throws CommentNotFoundException If the comment with the specified ID is not found.
     */
    @Override
    public CommentEntity getCommentById(Integer commentId) throws CommentApiException {
        if (commentId == null) {
            throw new CommentBadRequestException("Comment id can not be null");
        }
        Optional<CommentEntity> comment;
        try {
            comment = commentRepository.findById(commentId);
        } catch (Exception e) {
            throw new CommentApiException(ERROR_GETTING_COMMENTS);
        }

        if (comment.isEmpty()) {
            throw new CommentNotFoundException(COMMENT_NOT_FOUND);
        }
        return comment.get();
    }


    /**
     * Retrieves all comments for a specific task, performed by the user with the given email.
     *
     * @param taskId    The ID of the task for which comments are retrieved.
     * @param userEmail The email of the user attempting to retrieve comments.
     * @return List of CommentResponseDTO representing comments for the specified task.
     * @throws CommentApiException                   If an error occurs during comment retrieval.
     * @throws CommentUnauthorizedOperationException If the user is not authorized to perform the operation.
     */
    @Override
    public List<CommentResponseDTO> getCommentsByTaskId(Integer taskId, String userEmail) throws CommentApiException {

        if (taskId == null) {
            throw new CommentBadRequestException("Task id can not be null");
        }
        Optional<TaskEntity> task = taskRepository.findById(taskId);

        if (task.isEmpty()) {
            throw new TaskNotFoundException(TASK_NOT_FOUND);
        }
        if (!checkCommentPermission(task.get(), userEmail)) {
            throw new CommentUnauthorizedOperationException(UNAUTHORIZED_OPERATION_MSG);
        }
        try {
            return convertCommentEntitiesToDTOS(commentRepository.findByTaskTaskId(taskId));
        } catch (Exception e) {
            throw new CommentApiException(ERROR_GETTING_COMMENTS);
        }
    }


    /**
     * Updates the content of the comment with the specified ID, performed by the user with the given email.
     *
     * @param commentId The ID of the comment to be updated.
     * @param userEmail  The email of the user attempting to update the comment.
     * @param commentDTO The DTO containing the updated comment information.
     * @return CommentResponseDTO representing the updated comment.
     * @throws CommentApiException                   If an error occurs during comment update.
     * @throws CommentUnauthorizedOperationException If the user is not authorized to perform the operation.
     */
    @Override
    public CommentResponseDTO updateComment(Integer commentId, String userEmail, CommentDTO commentDTO) throws CommentApiException {
        CommentEntity comment = getCommentById(commentId);

        if (!comment.getUser().getEmail().equals(userEmail)) {
            throw new CommentUnauthorizedOperationException(UNAUTHORIZED_OPERATION_MSG);
        }
        comment.setContent(commentDTO.getText());
        return convertCommentEntityToDTO(saveComment(comment));
    }


    private CommentEntity saveComment(CommentEntity commentEntity) throws CommentApiException {
        try {
            return commentRepository.save(commentEntity);
        } catch (Exception e) {
            throw new CommentApiException(ERROR_SAVING_COMMENT);
        }
    }


    private boolean checkCommentPermission(TaskEntity task, String userEmail) {
        return task.getAuthor().getEmail().equals(userEmail) || task.getAssignee().getEmail().equals(userEmail);
    }
}