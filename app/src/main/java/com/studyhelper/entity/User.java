package com.studyhelper.entity;

import java.util.UUID;

public class User {
    private UUID id;
    private String name;
    private Integer age;

    public User(){

    }

    public User(String name, Integer age){
        this.name = name;
        this.age = age;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    // Геттер для поля id
    public UUID getId() {
        return id;
    }

    public int getAge() {
        return age;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return """
                USER{"name" = %s; "age" = %d}""".formatted(name, age);
    }
}
