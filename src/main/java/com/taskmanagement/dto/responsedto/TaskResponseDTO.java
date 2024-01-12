package com.taskmanagement.dto.responsedto;

import com.taskmanagement.dto.requestdto.CommentDTO;
import com.taskmanagement.enums.TaskPriority;
import com.taskmanagement.enums.TaskStatus;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TaskResponseDTO {
    private Integer taskId;
    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private String author;
    private String assignee;
    private List<CommentResponseDTO> comments;
}
