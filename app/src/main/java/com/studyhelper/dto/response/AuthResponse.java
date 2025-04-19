package com.studyhelper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Ответ с токенами после успешной аутентификации.
 */
@Schema(description = "Ответ с токенами")
public record AuthResponse(
        @Schema(description = "Access-токен")
        String accessToken,

        @Schema(description = "Refresh-токен")
        String refreshToken
) {
}