/*package com.studyhelper.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Ответ с информацией о сообщении в чате")
public record ChatMessageResponse(
        @Schema(description = "ID сообщения")
        UUID id,

        @Schema(description = "Ник отправителя")
        String senderNickname,

        @Schema(description = "Текст сообщения")
        String content,

        @Schema(description = "Дата и время отправки")
        LocalDateTime sentAt
) {}
 */