package com.studyhelper.controller;

import com.studyhelper.dto.request.ChatMessageRequest;
import com.studyhelper.dto.response.ChatResponse;
import com.studyhelper.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "Чаты", description = "API для управления чатами")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @Operation(summary = "Добавить сообщение в чат")
    @PostMapping(path = "/{taskId}/message", produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ChatResponse addMessage(@PathVariable UUID taskId, @RequestBody @Valid ChatMessageRequest request) {
        return chatService.addMessage(taskId, request);
    }

    @Operation(summary = "Получить чат по заданию")
    @GetMapping(path = "/{taskId}", produces = APPLICATION_JSON_VALUE)
    public ChatResponse getChatByTaskId(@PathVariable UUID taskId) {
        return chatService.getChatByTaskId(taskId);
    }
}