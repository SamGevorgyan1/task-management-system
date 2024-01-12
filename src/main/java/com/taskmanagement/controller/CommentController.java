package com.taskmanagement.controller;

import com.taskmanagement.dto.requestdto.CommentDTO;
import com.taskmanagement.dto.responsedto.CommentResponseDTO;
import com.taskmanagement.exceptions.CommentApiException;
import com.taskmanagement.exceptions.CommentBadRequestException;
import com.taskmanagement.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;


    @PostMapping("/{taskId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDTO createComment(@PathVariable Integer taskId,
                                            @RequestBody CommentDTO commentDTO,
                                            Principal principal) throws CommentApiException, CommentBadRequestException {
        return commentService.createComment(taskId, principal.getName(), commentDTO);
    }


    @PutMapping("/{commentId}")
    public CommentResponseDTO updateComment(@PathVariable Integer commentId,
                                            @RequestBody CommentDTO commentDTO,
                                            Principal principal) throws CommentApiException, CommentBadRequestException {
        return commentService.updateComment(commentId, principal.getName(), commentDTO);
    }

    @GetMapping
    public List<CommentResponseDTO> getCommentsBydTaskId(@RequestParam Integer taskId, Principal principal) throws CommentApiException {
        return commentService.getCommentsByTaskId(taskId, principal.getName());
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Integer commentId, Principal principal) throws CommentApiException {
        commentService.deleteComment(commentId, principal.getName());
    }
}