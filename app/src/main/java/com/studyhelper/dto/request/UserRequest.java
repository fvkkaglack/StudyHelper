package com.studyhelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Запрос на создание пользователя")
public record UserRequest(
        @NotBlank
        @Schema(description = "Никнейм пользователя", requiredMode = REQUIRED)
        String nickname
) {
}