package com.studyhelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Запрос на отклонение задачи")
public record RejectTaskRequest(
        @NotBlank
        @Schema(description = "Причина отклонения и создания спора", requiredMode = REQUIRED)
        String disputeReason
) {}