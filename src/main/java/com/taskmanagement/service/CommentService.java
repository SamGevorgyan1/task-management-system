package com.taskmanagement.service;

import com.taskmanagement.dto.requestdto.CommentDTO;
import com.taskmanagement.dto.responsedto.CommentResponseDTO;
import com.taskmanagement.exceptions.CommentApiException;
import com.taskmanagement.exceptions.CommentBadRequestException;
import com.taskmanagement.model.CommentEntity;
import com.taskmanagement.model.UserEntity;

import java.util.List;

public interface CommentService {


    CommentResponseDTO createComment(Integer taskId, String userEmail, CommentDTO commentDTO) throws CommentApiException;

    void deleteComment(Integer commentId, String userEmail) throws CommentApiException;

    CommentEntity getCommentById(Integer commentId) throws CommentApiException;

    List<CommentResponseDTO> getCommentsByTaskId(Integer taskId,String userEmail) throws CommentApiException;

    CommentResponseDTO updateComment(Integer commentId, String userEmail, CommentDTO commentDTO) throws CommentApiException;

}