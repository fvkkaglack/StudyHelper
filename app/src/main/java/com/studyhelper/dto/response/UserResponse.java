package com.studyhelper.dto.response;

import com.studyhelper.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Ответ с данными пользователя.
 */
@Schema(description = "Ответ с данными пользователя")
public record UserResponse(
        @Schema(description = "ID пользователя")
        UUID id,

        @Schema(description = "Никнейм пользователя")
        String nickname,

        @Schema(description = "Роль пользователя")
        Role role,

        @Schema(description = "Баланс пользователя")
        Integer balance,

        @Schema(description = "Общее количество звёзд")
        Integer totalStars,

        @Schema(description = "Долг пользователя")
        Integer debt,

        @Schema(description = "Количество созданных задач")
        Integer tasksCreated,

        @Schema(description = "Количество взятых задач")
        Integer tasksTaken,

        @Schema(description = "Количество просроченных фейковых задач")
        Integer overdueFakeTasks,

        @Schema(description = "Количество несправедливых отказов")
        Integer unjustRejections,

        @Schema(description = "Заблокировано ли создание задач")
        Boolean taskCreationBlocked,

        @Schema(description = "Заблокировано ли выполнение задач")
        Boolean taskTakingBlocked,

        @Schema(description = "Дата окончания блокировки")
        LocalDateTime blockUntil
) {
}