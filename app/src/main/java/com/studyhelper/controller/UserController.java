package com.studyhelper.controller;

import com.studyhelper.dto.request.UserRequest;
import com.studyhelper.dto.response.PageDto;
import com.studyhelper.dto.response.UserResponse;
import com.studyhelper.entity.Role;
import com.studyhelper.entity.User;
import com.studyhelper.service.JwtService;
import com.studyhelper.service.RefreshTokenService;
import com.studyhelper.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    public UserController(UserService userService, JwtService jwtService, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
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

    // Обновление пользователя по ID (для MODERATOR и ADMIN)
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Обновить пользователя по ID", description = "Обновляет данные пользователя по его ID (только для модераторов и админов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable @Parameter(description = "ID пользователя") UUID id,
            @RequestBody @Valid @Parameter(description = "Данные для обновления пользователя") UserRequest request,
            @RequestParam(value = "role", required = false) @Parameter(description = "Новая роль пользователя (опционально)") Role role,
            @AuthenticationPrincipal UserDetails currentUser) {
        // Только админ может менять роль
        if (role != null && !currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new SecurityException("Только администратор может изменять роль");
        }
        UserResponse userResponse = userService.updateUser(id, request, role);
        return ResponseEntity.ok(userResponse);
    }

    // Обновление текущего пользователя (для USER, MODERATOR, ADMIN)
    @PutMapping("/me")
    @Operation(summary = "Обновить текущего пользователя", description = "Обновляет данные текущего пользователя (для всех ролей, ADMIN может менять роли)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<User> updateCurrentUser(
            @RequestBody @Valid @Parameter(description = "Данные для обновления пользователя") User updatedUser,
            @AuthenticationPrincipal UserDetails currentUser) {
        User user = userService.updateUser(currentUser.getUsername(), updatedUser, currentUser);
        return ResponseEntity.ok(user);
    }

    // Удаление пользователя по ID (для MODERATOR и ADMIN)
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить пользователя по ID", description = "Удаляет пользователя по его ID (только для модераторов и админов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<Void> deleteUserById(
            @PathVariable @Parameter(description = "ID пользователя") UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // Удаление пользователя по никнейму (для ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/by-nickname/{nickname}")
    @Operation(summary = "Удалить пользователя по никнейму", description = "Удаляет пользователя по его никнейму (только для админов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<Void> deleteUserByNickname(
            @PathVariable @Parameter(description = "Никнейм пользователя") String nickname) {
        userService.deleteUser(nickname);
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
            @ApiResponse(responseCode = "401", description = "Недействительный access-токен",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        logger.info("Получен запрос на выход пользователя");

        // Извлекаем access-токен из заголовка
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            logger.warn("Заголовок Authorization отсутствует или некорректен");
            return ResponseEntity.status(401).body(Map.of("error", "Недействительный access-токен"));
        }

        String accessToken = authorizationHeader.substring(7); // Убираем "Bearer "
        logger.info("Извлечен access-токен: {}", accessToken);

        // Проверяем валидность access-токена
        String username;
        try {
            username = jwtService.extractUsername(accessToken);
        } catch (Exception e) {
            logger.warn("Ошибка при извлечении имени пользователя из access-токена: {}", e.getMessage());
            return ResponseEntity.status(401).body(Map.of("error", "Недействительный access-токен"));
        }

        if (username == null) {
            logger.warn("Имя пользователя не найдено в access-токене");
            return ResponseEntity.status(401).body(Map.of("error", "Недействительный access-токен"));
        }

        // Загружаем пользователя
        UserDetails userDetails;
        try {
            userDetails = userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            logger.warn("Пользователь не найден: {}", username);
            return ResponseEntity.status(401).body(Map.of("error", "Пользователь не найден"));
        }

        // Проверяем валидность access-токена
        if (!jwtService.isTokenValid(accessToken, userDetails)) {
            logger.warn("Access-токен недействителен или истек для пользователя: {}", username);
            return ResponseEntity.status(401).body(Map.of("error", "Недействительный access-токен"));
        }

        // Инвалидируем все refresh-токены пользователя
        refreshTokenService.deleteByUser(userDetails instanceof com.studyhelper.entity.User user ? user : null);
        logger.info("Все refresh-токены пользователя {} успешно инвалидированы", username);

        return ResponseEntity.ok(Map.of("message", "Успешный выход из системы"));
    }
}