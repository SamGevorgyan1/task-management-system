package com.taskmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.taskmanagement.enums.TaskPriority;
import com.taskmanagement.enums.TaskStatus;
import lombok.*;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "task")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Integer taskId;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    private TaskPriority priority;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private UserEntity author;

    @OneToOne
    @JsonIgnore
    @JoinColumn(name = "assignee_id", referencedColumnName = "id")
    private UserEntity assignee;

    @OneToMany(mappedBy = "task", fetch = FetchType.LAZY)
    private List<CommentEntity> comments;

}
