package com.studyhelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Запрос на создание задачи")
public record TaskRequest(
        @NotBlank
        @Schema(description = "Название задачи", requiredMode = REQUIRED, example = "Новая задача")
        String title,

        @NotBlank
        @Schema(description = "Описание задачи", requiredMode = REQUIRED, example = "Описание новой задачи")
        String description,

        @Min(5)
        @Schema(description = "Награда за выполнение задачи (минимально 5)", requiredMode = REQUIRED, example = "10")
        int reward,

        @NotNull
        @Schema(description = "Крайний срок выполнения задачи", requiredMode = REQUIRED, example = "2025-06-01T12:00:00")
        LocalDateTime deadline
) {
}