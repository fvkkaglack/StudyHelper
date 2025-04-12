package com.studyhelper.mapper;

import com.studyhelper.dto.request.TaskRequest;
import com.studyhelper.dto.response.TaskResponse;
import com.studyhelper.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface TaskMapper {
    @Mapping(target = "id", ignore = true) // id генерируется автоматически
    @Mapping(target = "status", expression = "java(request.status() != null ? request.status() : \"OPEN\")")
    Task toTask(TaskRequest request);

    @Mapping(target = "status", expression = "java(task.getStatus() != null ? task.getStatus() : null)")
    @Mapping(target = "requestIds", expression = "java(task.getRequests().stream().map(com.studyhelper.entity.Request::getId).collect(java.util.stream.Collectors.toList()))")
    TaskResponse toResponse(Task task);

    @Mapping(target = "id", ignore = true) // id не обновляем
    @Mapping(target = "authorId", source = "request.authorId")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "reward", source = "request.reward")
    @Mapping(target = "deadline", source = "request.deadline")
    @Mapping(target = "executorId", source = "request.executorId")
    @Mapping(target = "status", expression = "java(request.status() != null ? request.status() : task.getStatus())")
    Task updateTask(Task task, TaskRequest request);
}