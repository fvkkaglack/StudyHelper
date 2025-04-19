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
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер для управления пользователями.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "Пользователи", description = "API для управления пользователями")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Создание пользователя (для MODERATOR и ADMIN)
    @PreAuthorize("hasAnyRole('MODERATOR', 'ADMIN')")
    @PostMapping
    @Operation(summary = "Создать пользователя", description = "Создаёт нового пользователя на основе переданных данных (только для модераторов и админов)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно создан"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещён")
    })
    public ResponseEntity<UserResponse> createUser(
            @RequestBody @Valid @Parameter(description = "Данные для создания пользователя") UserRequest request,
            @RequestParam(value = "role", defaultValue = "USER") @Parameter(description = "Роль пользователя (по умолчанию USER)") Role role,
            @AuthenticationPrincipal UserDetails currentUser) {
        // Только админ может задавать роль отличную от USER
        if (role != Role.USER && !currentUser.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new SecurityException("Только администратор может задавать роль отличную от USER");
        }
        UserResponse userResponse = userService.createUser(request, role);
        return ResponseEntity.ok(userResponse);
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
}