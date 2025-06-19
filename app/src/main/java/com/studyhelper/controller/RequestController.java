package com.studyhelper.controller;

import com.studyhelper.dto.request.RequestRequest;
import com.studyhelper.dto.response.RequestResponse;
import com.studyhelper.service.RequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

@RestController
@RequestMapping("/api/v1/request")
@Tag(name = "Заявки", description = "API для управления заявками")
public class RequestController {
    private final RequestService requestService;
    private static final Logger logger = LoggerFactory.getLogger(RequestController.class);

    public RequestController(RequestService requestService) {
        this.requestService = requestService;
    }

    @PostMapping("/{taskId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Создать заявку на задание",
            description = "Создаёт новую заявку на задание для авторизованного пользователя",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заявка успешно создана"),
            @ApiResponse(responseCode = "400", description = "Недействительные данные запроса"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    })
    public ResponseEntity<RequestResponse> createRequest(
            @PathVariable UUID taskId,
            @RequestBody @Valid RequestRequest request,
            @AuthenticationPrincipal UserDetails currentUser) {
        logger.info("Создание заявки на задачу с ID: {} от пользователя: {}", taskId, currentUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(requestService.createRequest(request, taskId, currentUser));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Ошибка: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        logger.warn("Ошибка: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}