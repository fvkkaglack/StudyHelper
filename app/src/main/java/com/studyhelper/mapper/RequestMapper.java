package com.studyhelper.mapper;

import com.studyhelper.dto.request.RequestRequest;
import com.studyhelper.dto.response.RequestResponse;
import com.studyhelper.entity.Request;
import com.studyhelper.entity.Task;
import com.studyhelper.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "task", source = "task")
    @Mapping(target = "userId", expression = "java(user.getId())")
    @Mapping(target = "status", constant = "PENDING")
    @Mapping(target = "comment", source = "request.comment")
    Request toRequest(RequestRequest request, Task task, User user);

    RequestResponse toResponse(Request request);
}