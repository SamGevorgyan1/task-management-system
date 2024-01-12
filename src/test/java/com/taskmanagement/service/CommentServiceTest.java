package com.taskmanagement.service;

import com.taskmanagement.dto.requestdto.CommentDTO;
import com.taskmanagement.dto.responsedto.CommentResponseDTO;
import com.taskmanagement.enums.TaskPriority;
import com.taskmanagement.enums.TaskStatus;
import com.taskmanagement.enums.UserRole;
import com.taskmanagement.enums.UserStatus;
import com.taskmanagement.exceptions.*;
import com.taskmanagement.model.CommentEntity;
import com.taskmanagement.model.TaskEntity;
import com.taskmanagement.model.UserEntity;
import com.taskmanagement.repository.CommentRepository;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.repository.UserRepository;
import com.taskmanagement.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class CommentServiceTest {


    @Mock
    private CommentRepository commentRepository;

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private UserRepository userRepository;

    private UserEntity author = createUserEntity("author@gmail.com", 1);

    private TaskEntity task = createTaskEntity(author, createUserEntity("assignee", 2));


    CommentEntity commentEntity = CommentEntity.builder()
            .commentId(1)
            .content("Comment Text")
            .user(author)
            .task(task)
            .build();

    CommentDTO commentDTO = CommentDTO.builder()
            .text("Comment Comment")
            .build();


    @Test
    void createCommentTest() throws CommentApiException {
        given(commentRepository.save(any(CommentEntity.class))).willReturn(commentEntity);
        given(taskRepository.findById(any(Integer.class))).willReturn(Optional.ofNullable(task));
        given(userRepository.findByEmail(any(String.class))).willReturn(author);

        CommentResponseDTO comment = commentService.createComment(1, author.getEmail(), commentDTO);

        assertEquals(comment.getCommentId(), commentEntity.getCommentId());
        assertEquals(comment.getCreator(), commentEntity.getUser().getEmail());
        assertEquals(comment.getContent(), commentEntity.getContent());

        verify(commentRepository, times(1)).save(any());
        verify(userRepository, times(1)).findByEmail(any());
        verify(taskRepository, times(1)).findById(any());

        given(commentRepository.save(any(CommentEntity.class))).willThrow(RuntimeException.class);
        given(taskRepository.findById(any(Integer.class))).willReturn(Optional.ofNullable(task));
        assertThrows(CommentApiException.class, () -> commentService.createComment(1, author.getEmail(), commentDTO));

        assertThrows(CommentBadRequestException.class, () -> commentService.createComment(null, "email", commentDTO));

        given(taskRepository.findById(any(Integer.class))).willReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> commentService.createComment(any(Integer.class), "email", commentDTO));

        task.setAuthor(createUserEntity("user", 2));
        given(taskRepository.findById(any(Integer.class))).willReturn(Optional.ofNullable(task));
        assertThrows(CommentUnauthorizedOperationException.class, () -> commentService.createComment(any(Integer.class), "email", commentDTO));

    }

    @Test
    void deleteCommentTest() throws CommentApiException {

        given(commentRepository.findById(any(Integer.class))).willReturn(Optional.ofNullable(commentEntity));

        commentService.deleteComment(1, author.getEmail());

        verify(commentRepository).delete(commentEntity);

        given(commentRepository.findById(any(Integer.class))).willReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentService.deleteComment(1, author.getEmail()));

        assertThrows(CommentBadRequestException.class, () -> commentService.deleteComment(null, author.getEmail()));

        commentEntity.getUser().setEmail("user");
        given(commentRepository.findById(any(Integer.class))).willReturn(Optional.ofNullable(commentEntity));
        assertThrows(CommentUnauthorizedOperationException.class, () -> commentService.deleteComment(any(Integer.class), "email"));

        given(commentRepository.findById(any(Integer.class))).willThrow(RuntimeException.class);
        assertThrows(CommentApiException.class, () -> commentService.deleteComment(1, author.getEmail()));


    }

    @Test
    void getCommentByIdTest() throws CommentApiException {

        given(commentRepository.findById(any(Integer.class))).willReturn(Optional.ofNullable(commentEntity));

        CommentEntity comment = commentService.getCommentById(1);

        assertEquals(comment.getCommentId(), commentEntity.getCommentId());
        assertEquals(comment.getUser(), commentEntity.getUser());
        assertEquals(comment.getContent(), commentEntity.getContent());
        assertEquals(comment.getTask(), commentEntity.getTask());

        verify(commentRepository, times(1)).findById(any());


        given(commentRepository.findById(any(Integer.class))).willReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentService.getCommentById(1));

        assertThrows(CommentBadRequestException.class, () -> commentService.getCommentById(null));

        given(commentRepository.findById(any(Integer.class))).willReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentService.getCommentById(any(Integer.class)));

        given(commentRepository.findById(any(Integer.class))).willThrow(RuntimeException.class);
        assertThrows(CommentApiException.class, () -> commentService.getCommentById(1));

    }

    @Test
    void getCommentsByTaskIdTest() throws CommentApiException {
        CommentEntity comment1 = createCommentEntity(author);
        CommentEntity comment2 = createCommentEntity(author);

        List<CommentEntity> comments = new ArrayList<>(List.of(comment1, comment2));

        given(commentRepository.findByTaskTaskId(any(Integer.class))).willReturn(comments);
        task.setComments(comments);
        given(taskRepository.findById(any(Integer.class))).willReturn(Optional.ofNullable(task));


        assertDoesNotThrow(() -> {
            List<CommentResponseDTO> commentsResponseDTOs = commentService.getCommentsByTaskId(1, author.getEmail());
            assertNotNull(commentsResponseDTOs);
            assertEquals(2, commentsResponseDTOs.size());
            assertEquals(commentEntity.getContent(), commentsResponseDTOs.get(1).getContent());
            assertEquals(commentEntity.getUser().getEmail(), commentsResponseDTOs.get(1).getCreator());
            assertEquals(commentEntity.getCommentId(), commentsResponseDTOs.get(1).getCommentId());
        });
        verify(commentRepository, times(1)).findByTaskTaskId(any());

        given(commentRepository.findByTaskTaskId(any(Integer.class))).willThrow(RuntimeException.class);
        assertThrows(CommentApiException.class, () -> commentService.getCommentsByTaskId(1, author.getEmail()));

        given(taskRepository.findById(any(Integer.class))).willReturn(Optional.empty());
        assertThrows(TaskNotFoundException.class, () -> commentService.getCommentsByTaskId(1, author.getEmail()));

        assertThrows(CommentBadRequestException.class, () -> commentService.getCommentsByTaskId(null, author.getEmail()));


    }

    @Test
    void updateCommentTest() throws CommentApiException {
        given(commentRepository.save(any(CommentEntity.class))).willReturn(commentEntity);
        given(commentRepository.findById(1)).willReturn(Optional.ofNullable(commentEntity));

        CommentResponseDTO comment = commentService.updateComment(1, author.getEmail(), new CommentDTO("updated comment"));

        assertEquals(comment.getCommentId(), commentEntity.getCommentId());
        assertEquals(comment.getCreator(), commentEntity.getUser().getEmail());
        assertEquals(comment.getContent(), commentEntity.getContent());

        verify(commentRepository, times(1)).findById(any());

        assertThrows(CommentBadRequestException.class, () -> commentService.updateComment(null, author.getEmail(), new CommentDTO("updated comment")));

        given(commentRepository.findById(1)).willReturn(Optional.empty());
        assertThrows(CommentNotFoundException.class, () -> commentService.updateComment(1, author.getEmail(), new CommentDTO("updated comment")));
    }


    private UserEntity createUserEntity(String email, Integer id) {
        return UserEntity.builder()
                .id(id)
                .name("User")
                .surname("Surname")
                .email(email)
                .year(2000)
                .password("123456Aa")
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build();
    }

    private CommentEntity createCommentEntity(UserEntity author) {
        return CommentEntity.builder()
                .commentId(1)
                .content("Comment Text")
                .user(author)
                .task(new TaskEntity())
                .build();
    }

    private TaskEntity createTaskEntity(UserEntity author, UserEntity assignee) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTaskId(1);
        taskEntity.setTitle("Task Title");
        taskEntity.setDescription("Task description");
        taskEntity.setStatus(TaskStatus.IN_PROGRESS);
        taskEntity.setPriority(TaskPriority.HIGH);
        taskEntity.setAuthor(author);
        taskEntity.setAssignee(assignee);
        return taskEntity;
    }
}