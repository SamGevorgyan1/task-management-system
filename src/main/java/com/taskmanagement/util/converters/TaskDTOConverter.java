package com.taskmanagement.util.converters;

import com.taskmanagement.dto.responsedto.TaskResponseDTO;
import com.taskmanagement.model.TaskEntity;
import java.util.List;
import java.util.stream.Collectors;
import static com.taskmanagement.util.converters.CommentDTOConverter.convertCommentEntitiesToDTOS;

public class TaskDTOConverter {

    public static TaskResponseDTO convertTaskEntityToDTO(TaskEntity taskEntity) {
        return TaskResponseDTO.builder()
                .taskId(taskEntity.getTaskId())
                .title(taskEntity.getTitle())
                .description(taskEntity.getDescription())
                .status(taskEntity.getStatus())
                .priority(taskEntity.getPriority())
                .assignee(taskEntity.getAssignee().getEmail())
                .author(taskEntity.getAuthor().getEmail())
                .comments(convertCommentEntitiesToDTOS(taskEntity.getComments()))
                .build();
    }

    public static List<TaskResponseDTO> convertTaskEntityToDTOs(List<TaskEntity> taskEntities) {
        return taskEntities.stream()
                .map(TaskDTOConverter::convertTaskEntityToDTO)
                .collect(Collectors.toList());
    }
}