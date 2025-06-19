package com.studyhelper.controller;

import com.studyhelper.dto.request.UserRequest;
import com.studyhelper.dto.response.PageDto;
import com.studyhelper.dto.response.UserResponse;
import com.studyhelper.entity.Role;
import com.studyhelper.entity.User;
import com.studyhelper.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Контроллер для управления пользователями.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Пользователи", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Назначение роли модератора (только для ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/assign-moderator/{nickname}")
    @Operation(summary = "Назначить роль модератора", description = "Назначает роль модератора существующему пользователю (только для админов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Роль модератора успешно назначена"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<UserResponse> assignModeratorRole(
            @PathVariable @Parameter(description = "Никнейм пользователя") String nickname) {
        UserResponse userResponse = userService.assignModeratorRole(nickname);
        return ResponseEntity.ok(userResponse);
    }

    // Назначение роли администратора (только для ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/assign-admin/{nickname}")
    @Operation(summary = "Назначить роль администратора", description = "Назначает роль администратора существующему пользователю (только для админов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Роль администратора успешно назначена"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<UserResponse> assignAdminRole(
            @PathVariable @Parameter(description = "Никнейм пользователя") String nickname) {
        UserResponse userResponse = userService.assignAdminRole(nickname);
        return ResponseEntity.ok(userResponse);
    }

    // Удаление пользователя по ID (для MODERATOR и ADMIN)
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя по ID", description = "Удаляет пользователя по его ID (только для модераторов и админов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Нельзя удалить текущего пользователя"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<Void> deleteUserById(
            @PathVariable @Parameter(description = "ID пользователя") UUID id,
            @AuthenticationPrincipal UserDetails currentUser) {
        userService.deleteUser(id, currentUser);
        return ResponseEntity.ok().build();
    }

    // Удаление пользователя по никнейму (для ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/by-nickname/{nickname}")
    @Operation(summary = "Удалить пользователя по никнейму", description = "Удаляет пользователя по его никнейму (только для админов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Нельзя удалить текущего пользователя"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<Void> deleteUserByNickname(
            @PathVariable @Parameter(description = "Никнейм пользователя") String nickname,
            @AuthenticationPrincipal UserDetails currentUser) {
        userService.deleteUser(nickname, currentUser);
        return ResponseEntity.ok().build();
    }

    // Получение пагинированного списка пользователей (для MODERATOR и ADMIN)
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @GetMapping
    @Operation(summary = "Получить список пользователей", description = "Возвращает пагинированный список пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<PageDto<UserResponse>> getUsers(
            @Parameter(description = "Параметры пагинации (например, page=0&size=10)") Pageable pageable) {
        return ResponseEntity.ok(userService.getUserPage(pageable));
    }

    // Получение полного списка пользователей (для MODERATOR и ADMIN)
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @GetMapping("/list")
    @Operation(summary = "Получить полный список пользователей", description = "Возвращает полный список пользователей без пагинации")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список пользователей успешно получен"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<List<UserResponse>> getUserList() {
        return ResponseEntity.ok(userService.getUserList());
    }

    // Получение пользователя по ID (для MODERATOR и ADMIN)
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @GetMapping("/{id}")
    @Operation(summary = "Получить пользователя по ID", description = "Возвращает пользователя по его ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно получен"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable @Parameter(description = "ID пользователя") UUID id) {
        User user = userService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));
        return ResponseEntity.ok(userService.getUserMapper().toResponse(user));
    }

    // Выход пользователя (для авторизованных пользователей)
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    @Operation(summary = "Выход пользователя", description = "Завершает сессию пользователя, инвалидируя все его refresh-токены")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный выход из системы"),
            @ApiResponse(responseCode = "400", description = "Недействительный access-токен"),
            @ApiResponse(responseCode = "401", description = "Пользователь не найден")
    })
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authorizationHeader) {
        userService.logout(authorizationHeader);
        return ResponseEntity.ok(Map.of("message", "Успешный выход из системы"));
    }

    // Обработчики исключений
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentException ex) {
        logger.warn("Ошибка: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        logger.warn("Недопустимое действие: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(org.springframework.dao.DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolation(org.springframework.dao.DataIntegrityViolationException ex) {
        logger.error("Нарушение целостности данных: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Невозможно удалить пользователя из-за связанных данных");
    }
}