package com.studyhelper.mapper;

import com.studyhelper.dto.request.UserRequest;
import com.studyhelper.dto.response.UserResponse;
import com.studyhelper.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
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
    User toUser(UserRequest request);

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
    User updateUser(@MappingTarget User user, UserRequest request);
}