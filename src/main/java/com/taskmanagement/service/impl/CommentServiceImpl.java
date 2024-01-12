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
import static com.taskmanagement.util.messages.TaskErrorMessage.TASK_NOT_FOUND_MSG;


@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;


    @Override
    public CommentResponseDTO createComment(Integer taskId, String userEmail, CommentDTO commentDTO) throws CommentApiException {

        if (taskId == null) {
            throw new CommentBadRequestException("Task Id can not be null");
        }
        Optional<TaskEntity> task = taskRepository.findById(taskId);

        if (task.isEmpty()) {
            throw new TaskNotFoundException(TASK_NOT_FOUND_MSG);
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

    @Override
    public void deleteComment(Integer commentId, String userEmail) throws CommentApiException {
        CommentEntity comment = getCommentById(commentId);

        if (!comment.getUser().getEmail().equals(userEmail)) {
            throw new CommentUnauthorizedOperationException(UNAUTHORIZED_OPERATION_MSG);
        }
        try {
            commentRepository.delete(comment);
        } catch (Exception e) {
            throw new CommentApiException(ERROR_DELETING_COMMENT_MSG);
        }
    }

    @Override
    public CommentEntity getCommentById(Integer commentId) throws CommentApiException {
        if (commentId == null) {
            throw new CommentBadRequestException("Comment id can not be null");
        }
        Optional<CommentEntity> comment;
        try {
            comment = commentRepository.findById(commentId);
        } catch (Exception e) {
            throw new CommentApiException(ERROR_GETTING_COMMENTS_MSG);
        }

        if (comment.isEmpty()) {
            throw new CommentNotFoundException(COMMENT_NOT_FOUND_MSG);
        }
        return comment.get();
    }

    @Override
    public List<CommentResponseDTO> getCommentsByTaskId(Integer taskId, String userEmail) throws CommentApiException {

        if (taskId == null) {
            throw new CommentBadRequestException("Task id can not be null");
        }
        Optional<TaskEntity> task = taskRepository.findById(taskId);

        if (task.isEmpty()) {
            throw new TaskNotFoundException(TASK_NOT_FOUND_MSG);
        }
        if (!checkCommentPermission(task.get(), userEmail)) {
            throw new CommentUnauthorizedOperationException(UNAUTHORIZED_OPERATION_MSG);
        }
        try {
            return convertCommentEntitiesToDTOS(commentRepository.findByTaskTaskId(taskId));
        } catch (Exception e) {
            throw new CommentApiException(ERROR_GETTING_COMMENTS_MSG);
        }
    }


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
            throw new CommentApiException(ERROR_SAVING_COMMENT_MSG);
        }
    }

    private boolean checkCommentPermission(TaskEntity task, String userEmail) {
        return task.getAuthor().getEmail().equals(userEmail) || task.getAssignee().getEmail().equals(userEmail);
    }
}