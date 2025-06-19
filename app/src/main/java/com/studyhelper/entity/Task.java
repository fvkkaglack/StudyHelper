package com.studyhelper.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "author_id")
    private UUID authorId;

    @Column(name = "executor_id")
    private UUID executorId;

    @Column(name = "reward")
    private int reward;

    @Column(name = "deadline")
    private LocalDateTime deadline;
}