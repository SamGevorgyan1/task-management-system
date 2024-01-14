package com.taskmanagement.util.converters;

import com.taskmanagement.dto.responsedto.CommentResponseDTO;
import com.taskmanagement.model.CommentEntity;

import java.util.List;
import java.util.stream.Collectors;

public class CommentDTOConverter {


    public static CommentResponseDTO convertCommentEntityToDTO(CommentEntity commentEntity) {
        return CommentResponseDTO.builder()
                .commentId(commentEntity.getCommentId())
                .creator(commentEntity.getUser().getEmail())
                .content(commentEntity.getContent())
                .build();
    }


    public static List<CommentResponseDTO> convertCommentEntitiesToDTOS(List<CommentEntity> commentEntities) {
        return commentEntities.stream()
                .map(CommentDTOConverter::convertCommentEntityToDTO)
                .collect(Collectors.toList());
    }
}
