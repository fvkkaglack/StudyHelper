package com.studyhelper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Пользователь")
public record UserResponse(
        @Schema(description = "Идентификатор пользователя", requiredMode = REQUIRED)
        UUID id,
        @Schema(description = "Имя пользователя", requiredMode = REQUIRED)
        String name,
        @Schema(description = "Возраст пользователя", requiredMode = REQUIRED)
        Integer age
) {
}
