package com.studyhelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import static io.swagger.v3.oas.annotations.media.Schema.RequiredMode.REQUIRED;

@Schema(description = "Пользователь")
public record UserRequest(
        @Size(max = 100)
        @NotBlank
        @Schema(description = "Имя пользователя", requiredMode = REQUIRED)
        String name,

        @NotNull
        @Positive
        @Max(100)
        @Schema(description = "Возраст пользователя", requiredMode = REQUIRED)
        Integer age
) {
}
