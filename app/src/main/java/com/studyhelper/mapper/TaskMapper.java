package com.studyhelper.mapper;

import com.studyhelper.dto.request.TaskRequest;
import com.studyhelper.dto.response.TaskResponse;
import com.studyhelper.entity.Task;
import com.studyhelper.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;
import static org.mapstruct.ReportingPolicy.ERROR;

@Mapper(componentModel = SPRING, unmappedTargetPolicy = ERROR)
public interface TaskMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", expression = "java(user.getId())") // Устанавливаем authorId из User
    @Mapping(target = "executorId", ignore = true) // Игнорируем executorId при создании
    @Mapping(target = "status", constant = "OPEN") // Устанавливаем фиксированное значение
    @Mapping(target = "title", source = "request.title")
    @Mapping(target = "description", source = "request.description")
    @Mapping(target = "reward", source = "request.reward")
    @Mapping(target = "deadline", source = "request.deadline")
    Task toTask(TaskRequest request, User user);

    // Упрощаем toResponse, убираем логику получения никнеймов
    @Mapping(target = "authorNickname", ignore = true) // Будет установлено в сервисе
    @Mapping(target = "executorNickname", ignore = true) // Будет установлено в сервисе
    TaskResponse toResponse(Task task);
}