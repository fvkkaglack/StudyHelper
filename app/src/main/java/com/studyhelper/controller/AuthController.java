package com.studyhelper.controller;

import com.studyhelper.dto.request.LoginRequest;
import com.studyhelper.dto.request.RegisterRequest;
import com.studyhelper.dto.response.AuthResponse;
import com.studyhelper.service.JwtService;
import com.studyhelper.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    public AuthController(UserService userService, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
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
            System.out.println("Login request received for nickname: " + request.nickname());
            UserDetails userDetails = userService.loadUserByUsername(request.nickname());
            System.out.println("User loaded: " + userDetails.getUsername());
            if (!passwordEncoder.matches(request.password(), userDetails.getPassword())) {
                System.out.println("Password does not match for user: " + request.nickname());
                return ResponseEntity.status(401).body(new AuthResponse(null, null));
            }
            System.out.println("Password matches, generating tokens for user: " + request.nickname());
            Map<String, String> tokens = userService.generateTokens(userDetails);
            return ResponseEntity.ok(new AuthResponse(tokens.get("accessToken"), tokens.get("refreshToken")));
        } catch (UsernameNotFoundException e) {
            System.out.println("User not found: " + request.nickname());
            return ResponseEntity.status(401).body(new AuthResponse(null, null));
        } catch (Exception e) {
            System.out.println("Unexpected error during login: " + e.getMessage());
            throw e;
        }
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.put(error.getField(), error.getDefaultMessage());
        }
        return ResponseEntity.badRequest().body(errors);
    }
}