package com.studyhelper.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserTest {
    @DisplayName("simple user test")
    @Test
    void test() {
        var user = new User();

        assertNotNull(user);
    }
}
