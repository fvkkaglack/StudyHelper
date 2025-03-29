package com.studyhelper.service;

import com.studyhelper.entity.User;
import com.studyhelper.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void saveUser() {
    }

    public Page<User> getUserPage(Pageable pageable) {
        return userRepository.findAll(pageable);
    }
}
