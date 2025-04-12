package com.studyhelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Запрос на создание задачи")
public record TaskRequest(
        @NotNull
        @Schema(description = "ID автора задачи", requiredMode = REQUIRED)
        UUID authorId,

        @NotBlank
        @Schema(description = "Описание задачи", requiredMode = REQUIRED)
        String description,

        @Min(5)
        @Schema(description = "Награда за выполнение задачи (минимально 5)", requiredMode = REQUIRED)
        int reward,

        @NotNull
        @Schema(description = "Крайний срок выполнения задачи", requiredMode = REQUIRED)
        LocalDateTime deadline,

        @Schema(description = "Статус задачи (OPEN, TAKEN, COMPLETED, OVERDUE, DISPUTED)")
        String status,

        @Schema(description = "ID исполнителя")
        UUID executorId
) {
}