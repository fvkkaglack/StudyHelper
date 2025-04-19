package com.studyhelper.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "chats")
@Getter
@Setter
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "task_id")
    private UUID taskId;

    @Column(name = "author_id")
    private UUID authorId;

    @Column(name = "executor_id")
    private UUID executorId;

    @Column(name = "messages")
    private String messages;

}