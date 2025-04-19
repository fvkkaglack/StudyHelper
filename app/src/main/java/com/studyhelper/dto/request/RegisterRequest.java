package com.studyhelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Запрос для регистрации пользователя")
public record RegisterRequest(
        @NotBlank
        @Schema(description = "Никнейм пользователя", requiredMode = REQUIRED)
        String nickname,

        @NotBlank
        @Schema(description = "Пароль", requiredMode = REQUIRED)
        String password
) {
}