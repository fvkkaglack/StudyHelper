package com.studyhelper.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO) // Указываем, что id генерируется
    @Column(name = "id")
    private UUID id;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

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
    private boolean taskCreationBlocked;

    @Column(name = "is_task_taking_blocked")
    private boolean taskTakingBlocked;

    @Column(name = "block_until")
    private LocalDateTime blockUntil;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return nickname;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (blockUntil != null && blockUntil.isAfter(LocalDateTime.now())) {
            return false;
        }
        return !taskCreationBlocked && !taskTakingBlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void addToBalance(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Сумма пополнения не может быть отрицательной");
        }
        this.balance += amount;
    }

    public void subtractFromBalance(int amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Сумма списания не может быть отрицательной");
        }
        if (this.balance < amount) {
            throw new IllegalStateException("Недостаточно средств на балансе");
        }
        this.balance -= amount;
    }
}

