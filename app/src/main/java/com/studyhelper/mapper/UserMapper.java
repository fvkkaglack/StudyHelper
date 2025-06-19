package com.studyhelper.mapper;

import com.studyhelper.dto.request.UserRequest;
import com.studyhelper.dto.response.UserResponse;
import com.studyhelper.entity.Role;
import com.studyhelper.entity.User;
import com.studyhelper.service.PasswordService;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "nickname", source = "request.nickname")
    @Mapping(target = "password", expression = "java(passwordService.encodePassword(request.password()))")
    @Mapping(target = "role", constant = "USER")
    @Mapping(target = "balance", constant = "50") // Дублируем SQL для контроля
    @Mapping(target = "totalStars", constant = "0") // Дублируем SQL для контроля
    @Mapping(target = "debt", constant = "0") // Дублируем SQL для контроля
    @Mapping(target = "tasksCreated", constant = "0") // Дублируем SQL для контроля
    @Mapping(target = "tasksTaken", constant = "0") // Дублируем SQL для контроля
    @Mapping(target = "overdueFakeTasks", constant = "0") // Дублируем SQL для контроля
    @Mapping(target = "unjustRejections", constant = "0") // Дублируем SQL для контроля
    @Mapping(target = "taskCreationBlocked", constant = "false") // Дублируем SQL для контроля
    @Mapping(target = "taskTakingBlocked", constant = "false") // Дублируем SQL для контроля
    @Mapping(target = "blockUntil", ignore = true)
    User toUser(UserRequest request, PasswordService passwordService);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "nickname", source = "nickname")
    @Mapping(target = "role", source = "role")
    @Mapping(target = "balance", source = "balance")
    @Mapping(target = "totalStars", source = "totalStars")
    @Mapping(target = "debt", source = "debt")
    @Mapping(target = "tasksCreated", source = "tasksCreated")
    @Mapping(target = "tasksTaken", source = "tasksTaken")
    @Mapping(target = "overdueFakeTasks", source = "overdueFakeTasks")
    @Mapping(target = "unjustRejections", source = "unjustRejections")
    @Mapping(target = "taskCreationBlocked", source = "taskCreationBlocked")
    @Mapping(target = "taskTakingBlocked", source = "taskTakingBlocked")
    @Mapping(target = "blockUntil", source = "blockUntil")
    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "balance", ignore = true)
    @Mapping(target = "totalStars", ignore = true)
    @Mapping(target = "debt", ignore = true)
    @Mapping(target = "tasksCreated", ignore = true)
    @Mapping(target = "tasksTaken", ignore = true)
    @Mapping(target = "overdueFakeTasks", ignore = true)
    @Mapping(target = "unjustRejections", ignore = true)
    @Mapping(target = "taskCreationBlocked", ignore = true)
    @Mapping(target = "taskTakingBlocked", ignore = true)
    @Mapping(target = "blockUntil", ignore = true)
    @Mapping(target = "role", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    User updateUser(@MappingTarget User user, UserRequest request);
}