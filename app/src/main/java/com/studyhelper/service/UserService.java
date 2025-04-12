package com.studyhelper.service;

import com.studyhelper.dto.request.UserRequest;
import com.studyhelper.dto.response.PageDto;
import com.studyhelper.dto.response.UserResponse;
import com.studyhelper.entity.User;
import com.studyhelper.mapper.UserMapper;
import com.studyhelper.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse createUser(@Valid UserRequest request) {
        User user = userMapper.toUser(request);
        user.setId(UUID.randomUUID());
        User savedUser = userRepository.save(user);
        return userMapper.toResponse(savedUser);
    }

    public UserResponse updateUser(UUID id, @Valid UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        userMapper.updateUser(user, request);
        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }

    public List<UserResponse> getUserList() {
        return StreamSupport.stream(userRepository.findAll().spliterator(), false)
                .map(userMapper::toResponse)
                .toList();
    }

    public PageDto<UserResponse> getUserPage(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return new PageDto<>(
                page.getContent().stream().map(userMapper::toResponse).toList(),
                page.getNumber(),
                page.getNumberOfElements(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}