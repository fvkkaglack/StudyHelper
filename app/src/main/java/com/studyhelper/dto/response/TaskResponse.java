package com.studyhelper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Schema(description = "Ответ с данными задачи")
public record TaskResponse(
        @Schema(description = "ID задачи")
        UUID id,

        @Schema(description = "ID автора задачи")
        UUID authorId,

        @Schema(description = "Описание задачи")
        String description,

        @Schema(description = "Награда за выполнение задачи")
        int reward,

        @Schema(description = "Крайний срок выполнения задачи")
        LocalDateTime deadline,

        @Schema(description = "Статус задачи (OPEN, TAKEN, COMPLETED, OVERDUE, DISPUTED)")
        String status,

        @Schema(description = "ID исполнителя")
        UUID executorId,

        @Schema(description = "Список ID заявок на задачу")
        List<UUID> requestIds
) {
}