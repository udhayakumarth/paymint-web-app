package com.example.springapp.goals;


import com.example.springapp.user.UserEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;

@Entity
public class Goal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
    private double targetAmount;
    private String status;

    private Long targetDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private UserEntity user;

    public Goal() {
    }

    public Goal(String name, String description, double targetAmount, double currentAmount, UserEntity user,long targetDate) {
        this.name = name;
        this.description = description;
        this.targetAmount = targetAmount;
        this.user = user;
        this.targetDate = targetDate;
    }

    public Long getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(Long targetDate) {
        this.targetDate = targetDate;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Goals{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", targetAmount=" + targetAmount +
                '}';
    }
}
