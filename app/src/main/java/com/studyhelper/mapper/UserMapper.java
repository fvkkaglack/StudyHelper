package com.studyhelper.mapper;

import com.studyhelper.dto.request.UserRequest;
import com.studyhelper.dto.response.UserResponse;
import com.studyhelper.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.ERROR;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = ERROR)
public interface UserMapper {
    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
    User toUser(UserRequest request);

    @Mapping(target = "id", ignore = true)
    User updateUser(@MappingTarget User user, UserRequest request);
}
