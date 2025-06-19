package com.studyhelper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Schema(description = "Объект ответа для запроса")
public record RequestResponse(
        @Schema(description = "Уникальный идентификатор запроса", example = "123e4567-e89b-12d3-a456-426614174000")
        UUID id,

        @Schema(description = "Идентификатор задачи, связанной с запросом", example = "987fcdeb-12ab-34cd-5678-426614174000")
        UUID taskId,

        @Schema(description = "Идентификатор пользователя, создавшего запрос", example = "456e789f-56de-78ef-9012-426614174000")
        UUID userId,

        @Schema(description = "Статус запроса", example = "PENDING")
        String status
) {
}