package com.studyhelper.mapper;

import com.studyhelper.dto.request.UserRequest;
import com.studyhelper.dto.response.UserResponse;
import com.studyhelper.entity.User;
import org.mapstruct.*;

/**
 * Маппер для преобразования между User, UserRequest и UserResponse.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    /**
     * Преобразует UserRequest в User.
     *
     * @param request запрос на создание/обновление пользователя
     * @return сущность User
     */
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
    User toUser(UserRequest request);

    /**
     * Преобразует User в UserResponse.
     *
     * @param user сущность User
     * @return ответ с данными пользователя
     */
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

    /**
     * Обновляет существующего пользователя на основе UserRequest.
     *
     * @param user пользователь для обновления
     * @param request запрос с новыми данными
     * @return обновлённый пользователь
     */
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