package com.studyhelper.controller;

import com.studyhelper.dto.request.RejectTaskRequest;
import com.studyhelper.dto.request.TaskRequest;
import com.studyhelper.dto.response.PageDto;
import com.studyhelper.dto.response.TaskResponse;
import com.studyhelper.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/task")
@Tag(name = "Задачи", description = "API для управления задачами")
public class TaskController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @Operation(
            summary = "Создать задачу",
            description = "Создаёт новую задачу для авторизованного пользователя",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse createTask(
            @RequestBody @Valid TaskRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        LOGGER.info("Создание задачи от пользователя: {}", currentUser.getUsername());
        return taskService.createTask(request, currentUser);
    }

    @Operation(
            summary = "Удалить задачу",
            description = "Удаляет задачу по ID",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @DeleteMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable UUID taskId) {
        LOGGER.info("Удаление задачи с ID: {}", taskId);
        taskService.deleteTask(taskId);
    }

    @Operation(
            summary = "Получить страницу задач",
            description = "Возвращает страницу задач с пагинацией"
    )
    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public PageDto<TaskResponse> getTaskPage(Pageable pageable) {
        LOGGER.info("Получение страницы задач");
        return taskService.getTaskPage(pageable);
    }

    @Operation(
            summary = "Получить список всех задач",
            description = "Возвращает список всех задач"
    )
    @GetMapping(path = "/all", produces = APPLICATION_JSON_VALUE)
    public List<TaskResponse> getTaskList() {
        LOGGER.info("Получение списка всех задач");
        return taskService.getTaskList();
    }

    @Operation(
            summary = "Получить список открытых задач",
            description = "Возвращает список задач в статусе OPEN"
    )
    @GetMapping(path = "/open", produces = APPLICATION_JSON_VALUE)
    public List<TaskResponse> getOpenTasks() {
        LOGGER.info("Получение списка открытых задач");
        return taskService.getOpenTasks();
    }

    @Operation(
            summary = "Пометить задачу как выполненную",
            description = "Исполнитель помечает задачу как выполненную",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/{taskId}/complete", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public TaskResponse markTaskCompleted(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails currentUser) {
        LOGGER.info("Исполнитель помечает задачу с ID: {} как выполненную", taskId);
        return taskService.markTaskCompleted(taskId, currentUser);
    }

    @Operation(
            summary = "Подтвердить выполнение задачи",
            description = "Автор подтверждает выполнение задачи",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/{taskId}/confirm", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public TaskResponse confirmTaskCompletion(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails currentUser) {
        LOGGER.info("Автор подтверждает выполнение задачи с ID: {}", taskId);
        return taskService.confirmTaskCompletion(taskId, currentUser);
    }

    @Operation(
            summary = "Отклонить выполнение задачи",
            description = "Автор отклоняет выполнение задачи и создаёт спор",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/{taskId}/reject", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public TaskResponse rejectTaskCompletion(
            @PathVariable UUID taskId,
            @RequestBody @Valid RejectTaskRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        LOGGER.info("Автор отклоняет выполнение задачи с ID: {}", taskId);
        return taskService.rejectTaskCompletion(taskId, currentUser, request.disputeReason());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        LOGGER.warn("Ошибка: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        LOGGER.warn("Ошибка: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}