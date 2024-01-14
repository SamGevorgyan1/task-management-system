package com.taskmanagement.service;

import com.taskmanagement.exceptions.*;
import com.taskmanagement.model.CommentEntity;
import com.taskmanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.taskmanagement.dto.requestdto.TaskDTO;
import com.taskmanagement.dto.responsedto.TaskResponseDTO;
import com.taskmanagement.enums.TaskPriority;
import com.taskmanagement.enums.TaskStatus;
import com.taskmanagement.enums.UserRole;
import com.taskmanagement.enums.UserStatus;
import com.taskmanagement.model.TaskEntity;
import com.taskmanagement.model.UserEntity;
import com.taskmanagement.repository.TaskRepository;
import com.taskmanagement.service.impl.TaskServiceImpl;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    UserEntity author = createUserEntity("author@gmail.com", 1);

    UserEntity assignee = createUserEntity("assignee@gmail.com", 2);

    UserEntity user = createUserEntity("user", 3);

    TaskEntity taskEntity = createTaskEntity(author);

    @Test
    void createTaskTest() throws TaskApiException {
        given(taskRepository.save(any(TaskEntity.class))).willReturn(taskEntity);


        given(userRepository.findByEmail(author.getEmail())).willReturn(author);
        given(userRepository.findByEmail(assignee.getEmail())).willReturn(assignee);

        TaskResponseDTO taskResponseDTO = taskService.createTask(taskDTO, author.getEmail());

        assertEquals(taskEntity.getTaskId(), taskResponseDTO.getTaskId());
        assertEquals(taskEntity.getTitle(), taskResponseDTO.getTitle());
        assertEquals(taskEntity.getDescription(), taskResponseDTO.getDescription());
        assertEquals(taskEntity.getStatus(), taskResponseDTO.getStatus());
        assertEquals(taskEntity.getPriority(), taskResponseDTO.getPriority());
        assertEquals(taskEntity.getAuthor().getEmail(), taskResponseDTO.getAuthor());
        assertEquals(taskEntity.getAssignee().getEmail(), taskResponseDTO.getAssignee());

        verify(taskRepository, times(1)).save(any());
    }


    @Test
    void updateTaskByAuthorTest() throws TaskApiException, TaskBadRequestException, UserBadRequestException {

        given(taskRepository.findById(1)).willReturn(Optional.of(taskEntity));

        TaskDTO updatedTaskDTO = TaskDTO.builder()
                .title("Updated Task Title")
                .description("Updated Task description")
                .taskStatus("PENDING")
                .taskPriority("LOW")
                .assigneeEmail("new_assignee@gmail.com")
                .build();

        UserEntity newAssignee = createUserEntity("new_assignee@gmail.com", 4);

        given(userRepository.findByEmail("new_assignee@gmail.com")).willReturn(newAssignee);

        given(taskRepository.save(any(TaskEntity.class))).willReturn(taskEntity);

        TaskResponseDTO taskResponseDTO = taskService.updateTaskByAuthor(1, updatedTaskDTO, author.getEmail());

        assertEquals(updatedTaskDTO.getTitle(), taskResponseDTO.getTitle());
        assertEquals(updatedTaskDTO.getDescription(), taskResponseDTO.getDescription());
        assertEquals(Enum.valueOf(TaskStatus.class, updatedTaskDTO.getTaskStatus()), taskResponseDTO.getStatus());
        assertEquals(Enum.valueOf(TaskPriority.class, updatedTaskDTO.getTaskPriority()), taskResponseDTO.getPriority());
        assertEquals(taskEntity.getAuthor().getEmail(), taskResponseDTO.getAuthor());
        assertEquals(updatedTaskDTO.getAssigneeEmail(), taskResponseDTO.getAssignee());

        verify(taskRepository, times(1)).save(any());

        assertThrows(TaskUnauthorizedOperationException.class, () -> taskService.updateTaskByAuthor(1, taskDTO, user.getEmail()));

        given(userRepository.findByEmail("test12test@gmail.com")).willReturn(null);

        assertThrows(UserNotFoundException.class, () -> {
            taskDTO.setAssigneeEmail("test12test@gmail.com");
            taskService.updateTaskByAuthor(1, taskDTO, author.getEmail());
        });
    }


    @Test
    void updateTaskByAssigneeTest() throws Exception {
        given(taskRepository.findById(1)).willReturn(Optional.of(taskEntity));

        TaskResponseDTO taskResponseDTO = taskService.updateTaskByAssignee(1, "PENDING", assignee.getEmail());

        assertEquals(taskEntity.getTitle(), taskResponseDTO.getTitle());
        assertEquals(taskEntity.getDescription(), taskResponseDTO.getDescription());
        assertEquals(taskEntity.getStatus(), taskResponseDTO.getStatus());
        assertEquals(taskEntity.getPriority(), taskResponseDTO.getPriority());
        assertEquals(taskEntity.getAuthor().getEmail(), taskResponseDTO.getAuthor());
        assertEquals(taskEntity.getAssignee().getEmail(), taskResponseDTO.getAssignee());
        assertEquals(taskEntity.getStatus(), taskResponseDTO.getStatus());

        assertThrows(TaskBadRequestException.class, () -> taskService.updateTaskByAssignee(1, "PND", assignee.getEmail()));

        assertThrows(TaskBadRequestException.class, () -> taskService.updateTaskByAssignee(1, null, assignee.getEmail()));

        assertThrows(TaskBadRequestException.class, () -> taskService.updateTaskByAssignee(null, "PENDING", assignee.getEmail()));

        given(taskRepository.findById(any(Integer.class))).willThrow(RuntimeException.class);

        assertThrows(TaskApiException.class, () -> taskService.updateTaskByAssignee(1, "PENDING", assignee.getEmail()));
    }

    @Test
    void deleteTaskTest() throws TaskApiException, TaskBadRequestException {
        given(taskRepository.findById(1)).willReturn(Optional.ofNullable(taskEntity));

        taskService.deleteTask(1, author.getEmail());

        verify(taskRepository).delete(taskEntity);

        assertThrows(TaskUnauthorizedOperationException.class, () -> taskService.deleteTask(1, user.getEmail()));

        assertThrows(TaskBadRequestException.class, () -> taskService.deleteTask(null, assignee.getEmail()));

        given(taskRepository.findById(any(Integer.class))).willThrow(RuntimeException.class);
        assertThrows(TaskApiException.class, () -> taskService.deleteTask(1, author.getEmail()));
    }


    @Test
    void getTaskByIdTest() throws TaskApiException, TaskBadRequestException {
        given(taskRepository.findById(any(Integer.class))).willReturn(Optional.ofNullable(taskEntity));

        TaskEntity task = taskService.getTaskById(1);

        assertEquals(taskEntity.getTitle(), task.getTitle());
        assertEquals(taskEntity.getDescription(), task.getDescription());
        assertEquals(taskEntity.getStatus(), task.getStatus());
        assertEquals(taskEntity.getPriority(), task.getPriority());
        assertEquals(taskEntity.getAuthor(), task.getAuthor());
        assertEquals(taskEntity.getAssignee(), task.getAssignee());
        assertEquals(taskEntity.getStatus(), task.getStatus());

        assertThrows(TaskBadRequestException.class, () -> taskService.getTaskById(null));
        given(taskRepository.findById(any(Integer.class))).willReturn(Optional.empty());

        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(1));

        given(taskRepository.findById(any(Integer.class))).willThrow(RuntimeException.class);

        assertThrows(TaskApiException.class, () -> taskService.getTaskById(1));
    }


    @Test
    void getTasksByAuthorTest() {


        TaskEntity task1 = createTaskEntity(author);

        TaskEntity task2 = createTaskEntity(author);
        TaskEntity task3 = createTaskEntity(author);


        List<TaskEntity> taskEntities = new ArrayList<>(List.of(task1, task2, task3));


        given(taskRepository.findByAuthorEmailAndAssigneeEmail(author.getEmail(), assignee.getEmail())).willReturn(taskEntities);

        assertDoesNotThrow(() -> {
            List<TaskResponseDTO> tasks = taskService.getTasksByAuthor(author.getEmail(), assignee.getEmail(), null, null);
            assertNotNull(tasks);
            assertEquals(3, tasks.size());
            assertEquals("Task Title", tasks.get(0).getTitle());
        });

        taskEntities.get(2).setPriority(TaskPriority.MEDIUM);
        given(taskRepository.findByAuthorEmailAndAssigneeEmailAndPriority(author.getEmail(), assignee.getEmail(), TaskPriority.LOW)).willReturn(taskEntities);
        assertDoesNotThrow(() -> {
            List<TaskResponseDTO> tasks = taskService.getTasksByAuthor(author.getEmail(), assignee.getEmail(), null, "LOW");
            assertNotNull(tasks);
            assertEquals(3, tasks.size());
            assertEquals("Task Title", tasks.get(1).getTitle());
        });


        given(taskRepository.findByAuthorEmailAndAssigneeEmail(author.getEmail(), assignee.getEmail())).willThrow(RuntimeException.class);

        assertThrows(TaskApiException.class, () -> taskService.getTasksByAuthor(author.getEmail(), assignee.getEmail(), null, null));
    }


    @Test
    void getTasksByAssigneeTest() {
        String assigneeEmail = "assignee@example.com";
        String authorEmail = "author@example.com";

        UserEntity author = createUserEntity(authorEmail, 22);

        TaskEntity task1 = createTaskEntity(author);
        TaskEntity task2 = createTaskEntity(author);
        List<TaskEntity> tasksByAssignee = new ArrayList<>();
        tasksByAssignee.add(task1);
        tasksByAssignee.add(task2);

        given(taskRepository.findByAssigneeEmail(assigneeEmail)).willReturn(tasksByAssignee);

        List<TaskResponseDTO> tasks = assertDoesNotThrow(() -> taskService.getTasksByAssignee(assigneeEmail, null));

        assertNotNull(tasks);
        assertEquals(2, tasks.size());
        assertEquals(task1.getTitle(), tasks.get(0).getTitle());
        assertEquals(task2.getTitle(), tasks.get(1).getTitle());

        verify(taskRepository, times(1)).findByAssigneeEmail(assigneeEmail);


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


    TaskDTO taskDTO = TaskDTO.builder()
            .title("Task Title")
            .description("Task description")
            .taskStatus("IN_PROGRESS")
            .taskPriority("HIGH")
            .assigneeEmail("assignee@gmail.com")
            .build();


    private TaskEntity createTaskEntity(UserEntity author) {
        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setTaskId(1);
        taskEntity.setTitle("Task Title");
        taskEntity.setDescription("Task description");
        taskEntity.setStatus(TaskStatus.IN_PROGRESS);
        taskEntity.setPriority(TaskPriority.HIGH);
        taskEntity.setAuthor(author);
        taskEntity.setAssignee(assignee);
        taskEntity.setComments(List.of(new CommentEntity(1, "", assignee, null)));
        return taskEntity;
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