package com.studyhelper.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "disputes")
public class Dispute {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "task_id")
    private UUID taskId;

    @Column(name = "complainant_id")
    private UUID complainantId;

    @Column(name = "reason")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private DisputeStatus status;

    @Column(name = "resolution")
    private String resolution;

    public Dispute() {
    }

    // Геттеры и сеттеры
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getTaskId() {
        return taskId;
    }

    public void setTaskId(UUID taskId) {
        this.taskId = taskId;
    }

    public UUID getComplainantId() {
        return complainantId;
    }

    public void setComplainantId(UUID complainantId) {
        this.complainantId = complainantId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public DisputeStatus getStatus() {
        return status;
    }

    public void setStatus(DisputeStatus status) {
        this.status = status;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public enum DisputeStatus {
        PENDING, RESOLVED, ESCALATED
    }
}