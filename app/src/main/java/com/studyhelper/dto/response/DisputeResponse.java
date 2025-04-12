package com.studyhelper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Ответ с данными спора")
public record DisputeResponse(
        @Schema(description = "ID спора")
        UUID id,

        @Schema(description = "ID задачи")
        UUID taskId,

        @Schema(description = "ID заявителя")
        UUID complainantId,

        @Schema(description = "Причина спора")
        String reason,

        @Schema(description = "Статус спора (PENDING, RESOLVED, ESCALATED)")
        String status,

        @Schema(description = "Решение по спору")
        String resolution
) {
}