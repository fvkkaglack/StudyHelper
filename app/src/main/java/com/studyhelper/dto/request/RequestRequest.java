package com.studyhelper.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Запрос на создание заявки")
public record RequestRequest(
        @Schema(description = "Комментарий к заявке (опционально)", example = "Я хочу выполнить эту задачу")
        String comment
) {}