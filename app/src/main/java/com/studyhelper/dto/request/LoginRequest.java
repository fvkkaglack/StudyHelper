package com.studyhelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

/**
 * Запрос для входа пользователя или администратора.
 */
@Schema(description = "Запрос для входа")
public record LoginRequest(
        @NotBlank
        @Schema(description = "Никнейм пользователя (или имя пользователя для админа)", requiredMode = REQUIRED)
        String username,

        @NotBlank
        @Schema(description = "Пароль", requiredMode = REQUIRED)
        String password
) {
}