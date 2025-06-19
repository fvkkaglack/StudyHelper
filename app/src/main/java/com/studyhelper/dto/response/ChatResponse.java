package com.studyhelper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Ответ с данными чата")
public record ChatResponse(
        @Schema(description = "ID чата")
        UUID id,

        @Schema(description = "ID задачи")
        UUID taskId,

        @Schema(description = "ID автора")
        UUID authorId,

        @Schema(description = "ID исполнителя")
        UUID executorId,

        @Schema(description = "Список сообщений")
        List<ChatMessageResponse> messages
) {}