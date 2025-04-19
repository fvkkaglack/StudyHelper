package com.studyhelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * Запрос на создание или обновление пользователя.
 */
@Schema(description = "Запрос на создание пользователя")
public record UserRequest(
        @NotBlank
        @Schema(description = "Никнейм пользователя", requiredMode = REQUIRED)
        String nickname,

        @NotBlank
        @Schema(description = "Пароль пользователя", requiredMode = REQUIRED)
        String password
) {
}