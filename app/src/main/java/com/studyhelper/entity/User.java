package com.studyhelper.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "balance")
    private int balance;

    @Column(name = "total_stars")
    private int totalStars;

    @Column(name = "debt")
    private int debt;

    @Column(name = "tasks_created")
    private int tasksCreated;

    @Column(name = "tasks_taken")
    private int tasksTaken;

    @Column(name = "overdue_fake_tasks")
    private int overdueFakeTasks;

    @Column(name = "unjust_rejections")
    private int unjustRejections;

    @Column(name = "is_task_creation_blocked")
    private boolean isTaskCreationBlocked;

    @Column(name = "is_task_taking_blocked")
    private boolean isTaskTakingBlocked;

    @Column(name = "block_until")
    private LocalDateTime blockUntil;

    public User() {
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getTotalStars() {
        return totalStars;
    }

    public void setTotalStars(int totalStars) {
        this.totalStars = totalStars;
    }

    public int getDebt() {
        return debt;
    }

    public void setDebt(int debt) {
        this.debt = debt;
    }

    public int getTasksCreated() {
        return tasksCreated;
    }

    public void setTasksCreated(int tasksCreated) {
        this.tasksCreated = tasksCreated;
    }

    public int getTasksTaken() {
        return tasksTaken;
    }

    public void setTasksTaken(int tasksTaken) {
        this.tasksTaken = tasksTaken;
    }

    public int getOverdueFakeTasks() {
        return overdueFakeTasks;
    }

    public void setOverdueFakeTasks(int overdueFakeTasks) {
        this.overdueFakeTasks = overdueFakeTasks;
    }

    public int getUnjustRejections() {
        return unjustRejections;
    }

    public void setUnjustRejections(int unjustRejections) {
        this.unjustRejections = unjustRejections;
    }

    public boolean getTaskCreationBlocked() {
        return isTaskCreationBlocked;
    }

    public void setTaskCreationBlocked(boolean taskCreationBlocked) {
        isTaskCreationBlocked = taskCreationBlocked;
    }

    public boolean getTaskTakingBlocked() {
        return isTaskTakingBlocked;
    }

    public void setTaskTakingBlocked(boolean taskTakingBlocked) {
        isTaskTakingBlocked = taskTakingBlocked;
    }

    public LocalDateTime getBlockUntil() {
        return blockUntil;
    }

    public void setBlockUntil(LocalDateTime blockUntil) {
        this.blockUntil = blockUntil;
    }
}