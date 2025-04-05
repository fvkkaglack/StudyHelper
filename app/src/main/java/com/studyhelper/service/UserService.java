package com.studyhelper.service;

import com.studyhelper.dto.request.UserRequest;
import com.studyhelper.dto.response.PageDto;
import com.studyhelper.dto.response.UserResponse;
import com.studyhelper.mapper.UserMapper;
import com.studyhelper.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository,
                       UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public UserResponse createUser(UserRequest request) {
        var user = userMapper.toUser(request);
        return userMapper.toResponse(userRepository.save(user));
    }

    public PageDto<UserResponse> getUserPage(Pageable pageable) {
        return new PageDto<UserResponse>(userRepository.findAll(pageable), userMapper::toResponse);
    }

    public UserResponse updateUser(UUID id, UserRequest request) {
        var user = userRepository.findById(id).orElseThrow();
        var updatedUser = userRepository.save(userMapper.updateUser(user, request));
        return userMapper.toResponse(updatedUser);
    }

    public void deleteUser(UUID id) {
        userRepository.deleteById(id);
    }
}
