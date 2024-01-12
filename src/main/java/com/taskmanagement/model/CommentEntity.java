package com.taskmanagement.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "comment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Integer commentId;

    private String content;

    @OneToOne
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private UserEntity user;


    @ManyToOne
    @JoinColumn(name = "task_id")
    private TaskEntity task;
}