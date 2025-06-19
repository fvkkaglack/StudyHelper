package com.studyhelper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Ответ с данными о задаче")
public record TaskResponse(
        @Schema(description = "ID задачи", example = "550e8400-e29b-41d4-a716-446655440000")
        UUID id,

        @Schema(description = "Никнейм автора задачи", example = "author123")
        String authorNickname,

        @Schema(description = "Название задачи", example = "Новая задача")
        String title,

        @Schema(description = "Описание задачи", example = "Описание новой задачи")
        String description,

        @Schema(description = "Награда за выполнение задачи", example = "10")
        int reward,

        @Schema(description = "Крайний срок выполнения задачи", example = "2025-06-01T12:00:00")
        LocalDateTime deadline,

        @Schema(description = "Статус задачи", example = "OPEN")
        String status,

        @Schema(description = "Никнейм исполнителя (если назначен)", example = "executor123")
        String executorNickname
) {
}