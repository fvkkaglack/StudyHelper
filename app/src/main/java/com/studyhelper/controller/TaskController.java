package com.studyhelper.controller;

import com.studyhelper.dto.request.TaskRequest;
import com.studyhelper.dto.response.PageDto;
import com.studyhelper.dto.response.TaskResponse;
import com.studyhelper.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/task")
@Tag(name = "Задания", description = "API для управления заданиями")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(summary = "Создаёт задание")
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(@RequestBody @Valid TaskRequest request) {
        return taskService.createTask(request);
    }

    @Operation(summary = "Обновляет задание")
    @PutMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    public TaskResponse updateTask(@PathVariable UUID id, @RequestBody @Valid TaskRequest request) {
        return taskService.updateTask(id, request);
    }

    @Operation(summary = "Удаляет задание")
    @DeleteMapping(path = "/{id}", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable UUID id) {
        taskService.deleteTask(id);
    }

    @Operation(summary = "Предоставляет страницу заданий")
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public PageDto<TaskResponse> getTaskPage(@ParameterObject @PageableDefault(sort = "deadline") Pageable pageable) {
        return taskService.getTaskPage(pageable);
    }

    @Operation(summary = "Выбрать исполнителя для задания")
    @PostMapping(path = "/{id}/assign/{executorId}", produces = APPLICATION_JSON_VALUE)
    public TaskResponse assignExecutor(@PathVariable UUID id, @PathVariable UUID executorId) {
        return taskService.assignExecutor(id, executorId);
    }
}
