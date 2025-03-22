package com.studyhelper.entity;
import java.time.LocalDateTime;

public class Order {
    private String Name;
    private String Description;
    private LocalDateTime Deadline;
    private Double reward;

    public Order(String name, String Description, LocalDateTime deadline, double Reward) {
        this.Name = name;
        this.Description = Description;
        this.Deadline = deadline;
        this.reward = Reward;
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
                "orderName='" + Name + '\'' +
                ", orderDescription='" + Description + '\'' +
                ", deadline=" + Deadline +
                ", reward=" + reward +
                '}';
    }
}
