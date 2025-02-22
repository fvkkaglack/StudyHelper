package com.studyhelper.entity;

public class User {
    private final String name;
    private final int age;

    public User(String name, int age){
        this.name = name;
        this.age = age;
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
