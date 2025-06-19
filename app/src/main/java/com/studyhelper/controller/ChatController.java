package com.studyhelper.controller;

import com.studyhelper.dto.request.ChatMessageRequest;
import com.studyhelper.dto.response.ChatResponse;
import com.studyhelper.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "Чаты", description = "API для управления чатами")
public class ChatController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "Добавить сообщение в чат")
    @PostMapping(path = "/{taskId}/message", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("isAuthenticated()")
    public ChatResponse addMessage(
            @PathVariable UUID taskId,
            @RequestBody @Valid ChatMessageRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        LOGGER.info("Запрос на добавление сообщения в чат для задачи с ID: {} от пользователя: {}", taskId, currentUser.getUsername());
        return chatService.addMessage(taskId, request, currentUser);
    }

    @Operation(summary = "Получить чат по заданию")
    @GetMapping(path = "/{taskId}", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ChatResponse getChatByTaskId(
            @PathVariable UUID taskId,
            @AuthenticationPrincipal UserDetails currentUser) {
        LOGGER.info("Запрос на получение чата для задачи с ID: {} от пользователя: {}", taskId, currentUser.getUsername());
        return chatService.getChatByTaskId(taskId, currentUser);
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