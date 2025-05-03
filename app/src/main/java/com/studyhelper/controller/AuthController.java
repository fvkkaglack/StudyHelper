package com.studyhelper.controller;

import com.studyhelper.dto.request.LoginRequest;
import com.studyhelper.dto.request.RefreshTokenRequest;
import com.studyhelper.dto.request.RegisterRequest;
import com.studyhelper.dto.response.AuthResponse;
import com.studyhelper.dto.response.TokenResponse;
import com.studyhelper.entity.RefreshToken;
import com.studyhelper.service.JwtService;
import com.studyhelper.service.RefreshTokenService;
import com.studyhelper.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Аутентификация", description = "API для регистрации и входа пользователей")
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder, RefreshTokenService refreshTokenService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/register/user")
    @Operation(summary = "Регистрация пользователя", description = "Регистрирует нового пользователя с ролью USER")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно зарегистрирован, возвращены токены"),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса (например, никнейм уже занят)")
    })
    public ResponseEntity<AuthResponse> registerUser(@RequestBody @Valid RegisterRequest request) {
        Map<String, String> tokens = userService.register(request.nickname(), request.password());
        return ResponseEntity.ok(new AuthResponse(tokens.get("accessToken"), tokens.get("refreshToken")));
    }

    @PostMapping("/login")
    @Operation(summary = "Вход пользователя", description = "Аутентифицирует пользователя и возвращает токены")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход, возвращены токены"),
            @ApiResponse(responseCode = "401", description = "Неверные учетные данные")
    })
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest request) {
        try {
            logger.info("Login request received for nickname: {}", request.nickname());
            UserDetails userDetails = userService.loadUserByUsername(request.nickname());
            logger.info("User loaded: {}", userDetails.getUsername());
            if (!passwordEncoder.matches(request.password(), userDetails.getPassword())) {
                logger.warn("Password does not match for user: {}", request.nickname());
                return ResponseEntity.status(401).body(new AuthResponse(null, null));
            }
            logger.info("Password matches, generating tokens for user: {}", request.nickname());
            Map<String, String> tokens = userService.generateTokens(userDetails);
            return ResponseEntity.ok(new AuthResponse(tokens.get("accessToken"), tokens.get("refreshToken")));
        } catch (UsernameNotFoundException e) {
            logger.warn("User not found: {}", request.nickname());
            return ResponseEntity.status(401).body(new AuthResponse(null, null));
        } catch (Exception e) {
            logger.error("Unexpected error during login: {}", e.getMessage());
            throw e;
        }
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Обновление токена", description = "Обновляет access-токен с использованием refresh-токена")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Токены успешно обновлены",
                    content = @Content(schema = @Schema(implementation = TokenResponse.class))),
            @ApiResponse(responseCode = "401", description = "Недействительный или истёкший refresh-токен",
                    content = @Content(schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                    content = @Content(schema = @Schema(implementation = Map.class)))
    })
    public ResponseEntity<TokenResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        logger.info("Получен запрос на обновление токена для refreshToken: {}", request.refreshToken());

        String refreshToken = request.refreshToken();

        // Проверяем валидность текущего refresh-токена
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            logger.warn("Недействительный или истёкший refresh-токен: {}", refreshToken);
            return ResponseEntity.status(401).body(new TokenResponse(null, null));
        }

        // Находим текущий refresh-токен в базе
        RefreshToken existingToken = refreshTokenService.findByToken(refreshToken);
        String username = jwtService.extractUsername(refreshToken);
        logger.info("Извлечен пользователь: {}", username);

        // Загружаем пользователя
        UserDetails userDetails;
        try {
            userDetails = userService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            logger.warn("Пользователь не найден: {}", username);
            return ResponseEntity.status(401).body(new TokenResponse(null, null));
        }

        // Генерируем новый access-токен
        String newAccessToken = jwtService.generateAccessToken(userDetails);

        // Генерируем новый refresh-токен
        String newRefreshToken = jwtService.generateRefreshToken(userDetails);

        // Инвалидируем старый refresh-токен
        refreshTokenService.invalidateToken(refreshToken);
        logger.info("Старый refresh-токен инвалидирован: {}", refreshToken);

        logger.info("Токены успешно обновлены для пользователя: {}", username);
        return ResponseEntity.ok(new TokenResponse(newAccessToken, newRefreshToken));
    }

}