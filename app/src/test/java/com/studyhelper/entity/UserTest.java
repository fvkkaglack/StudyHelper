package com.studyhelper.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    @DisplayName("simple user test")
    @Test
    void test(){
        var user = new User("Vitya", 10);

        assertEquals(10, user.getAge());
        assertEquals("Vitya", user.getName());
        assertEquals("USER{\"name\" = Vitya; \"age\" = 10}", user.toString());
    }
}
