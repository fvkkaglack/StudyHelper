package com.studyhelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Запрос на создание спора")
public record DisputeRequest(

        @NotBlank
        @Schema(description = "Причина спора", requiredMode = REQUIRED)
        String reason
) {}