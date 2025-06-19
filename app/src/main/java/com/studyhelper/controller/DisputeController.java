package com.studyhelper.controller;

import com.studyhelper.dto.request.DisputeRequest;
import com.studyhelper.dto.response.DisputeResponse;
import com.studyhelper.service.DisputeService;
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
@RequestMapping("/api/v1/dispute")
@Tag(name = "Споры", description = "API для управления спорами")
public class DisputeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(DisputeController.class);
    private final DisputeService disputeService;

    public DisputeController(DisputeService disputeService) {
        this.disputeService = disputeService;
    }

    @Operation(
            summary = "Создать спор по заданию",
            description = "Создаёт спор для задачи, указывая причину. Доступно для исполнителя после отклонения.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PostMapping(path = "/task/{taskId}", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    @ResponseStatus(HttpStatus.CREATED)
    public DisputeResponse createDispute(
            @PathVariable UUID taskId,
            @RequestBody @Valid DisputeRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        LOGGER.info("Создание спора для задачи с ID: {} от пользователя: {}", taskId, currentUser.getUsername());
        return disputeService.createDispute(taskId, request, currentUser);
    }

    @Operation(
            summary = "Разрешить спор",
            description = "Разрешает спор, указывая решение. Доступно для модераторов или администраторов.",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @PutMapping(path = "/{id}/resolve", produces = APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    public DisputeResponse resolveDispute(@PathVariable UUID id, @RequestBody String resolution) {
        LOGGER.info("Разрешение спора с ID: {}", id);
        return disputeService.resolveDispute(id, resolution);
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