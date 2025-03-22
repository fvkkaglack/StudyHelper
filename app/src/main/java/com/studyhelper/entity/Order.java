package com.studyhelper.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class Order {
    private UUID id;  // Добавляем поле id типа UUID
    private String Name;
    private String Description;
    private LocalDateTime Deadline;
    private Double reward;

    // Обновляем конструктор, чтобы принимать id как параметр
    public Order(UUID id, String name, String description, LocalDateTime deadline, double reward) {
        this.id = id;
        this.Name = name;
        this.Description = description;
        this.Deadline = deadline;
        this.reward = reward;
    }

    public Order(){


    }

    // Геттер для id
    public UUID getId() {
        return id;
    }

    // Сеттер для id
    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public LocalDateTime getDeadline() {
        return Deadline;
    }

    public void setDeadline(LocalDateTime deadline) {
        this.Deadline = deadline;
    }

    public Double getReward() {
        return reward;
    }

    public void setReward(Double reward) {
        this.reward = reward;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +  // Добавляем id в строку
                ", orderName='" + Name + '\'' +
                ", orderDescription='" + Description + '\'' +
                ", deadline=" + Deadline +
                ", reward=" + reward +
                '}';
    }
}
