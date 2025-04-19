package com.studyhelper.controller;

import com.studyhelper.entity.User;
import com.studyhelper.service.JwtService;
import com.studyhelper.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "API для регистрации и входа пользователей")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;

    public AuthController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/register/user")
    @Operation(summary = "Регистрация пользователя", description = "Регистрирует нового пользователя с ролью USER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован, возвращены токены"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса (например, никнейм уже занят)")
    })
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody User user) {
        Map<String, String> tokens = userService.register(user.getNickname(), user.getPassword());
        return ResponseEntity.ok(tokens);
    }

    @PostMapping("/login")
    @Operation(summary = "Вход пользователя", description = "Аутентифицирует пользователя и возвращает токены")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход, возвращены токены"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    public ResponseEntity<Map<String, String>> login(@RequestBody User user) {
        UserDetails userDetails = userService.loadUserByUsername(user.getNickname());
        return ResponseEntity.ok(userService.generateTokens(userDetails));
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Обновление токена", description = "Обновляет access-токен с использованием refresh-токена")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токены успешно обновлены"),
            @ApiResponse(responseCode = "401", description = "Недействительный или истёкший refresh-токен")
    })
    public ResponseEntity<Map<String, String>> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");
        if (refreshToken == null || !jwtService.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity.status(401).body(Map.of("error", "Недействительный или истёкший refresh-токен"));
        }

        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userService.loadUserByUsername(username);
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken, "refreshToken", refreshToken));
    }
}