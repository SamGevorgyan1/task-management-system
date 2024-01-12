package com.taskmanagement.dto.responsedto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDTO {

    private Integer commentId;
    private String creator;
    private String content;
}
