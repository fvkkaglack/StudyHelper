package com.studyhelper.service;

import com.studyhelper.entity.RefreshToken;
import com.studyhelper.entity.User;
import com.studyhelper.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для работы с refresh-токенами.
 */
@Service
public class RefreshTokenService {

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    /**
     * Создаёт refresh-токен для пользователя.
     *
     * @param user пользователь
     * @param token значение токена
     * @return созданный refresh-токен
     */
    public RefreshToken createRefreshToken(User user, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user); // Используем setUser вместо setUserId
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000));
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Находит refresh-токен по его значению.
     *
     * @param token значение токена
     * @return refresh-токен
     * @throws IllegalArgumentException если токен не найден
     */
    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found: " + token));
    }

    /**
     * Находит refresh-токен по его значению (возвращает Optional).
     *
     * @param token значение токена
     * @return Optional с refresh-токеном, если найден, или пустой Optional
     */
    public Optional<RefreshToken> findByTokenOptional(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    /**
     * Проверяет, действителен ли refresh-токен.
     *
     * @param token refresh-токен
     * @return true, если токен действителен, иначе false
     */
    public boolean isRefreshTokenValid(RefreshToken token) {
        return token.getExpiresAt().isAfter(LocalDateTime.now());
    }

    /**
     * Удаляет все refresh-токены пользователя.
     *
     * @param user пользователь
     */
    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user); // Используем deleteByUser вместо deleteByUserId
    }
}