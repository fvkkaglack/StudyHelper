package com.studyhelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Запрос на создание спора")
public record DisputeRequest(
        @NotNull
        @Schema(description = "ID задачи", requiredMode = REQUIRED)
        UUID taskId,

        @NotNull
        @Schema(description = "ID заявителя", requiredMode = REQUIRED)
        UUID complainantId,

        @NotBlank
        @Schema(description = "Причина спора", requiredMode = REQUIRED)
        String reason,

        @Schema(description = "Статус спора (PENDING, RESOLVED, ESCALATED)")
        String status,

        @Schema(description = "Решение по спору")
        String resolution
) {
}
