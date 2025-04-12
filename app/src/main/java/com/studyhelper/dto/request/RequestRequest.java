package com.studyhelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Запрос на создание заявки")
public record RequestRequest(
        @NotNull
        @Schema(description = "ID задания", requiredMode = REQUIRED)
        UUID taskId,

        @NotNull
        @Schema(description = "ID исполнителя", requiredMode = REQUIRED)
        UUID executorId
) {
}