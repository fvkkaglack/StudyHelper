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

@Service
public class RefreshTokenService {

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(User user, String token) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(token);
        refreshToken.setExpiresAt(LocalDateTime.now().plusSeconds(refreshTokenExpiration / 1000));
        return refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findByToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found: " + token));
    }

    public Optional<RefreshToken> findByTokenOptional(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public boolean isRefreshTokenValid(RefreshToken token) {
        return token.getExpiresAt().isAfter(LocalDateTime.now());
    }

    @Transactional
    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    // Новый метод для инвалидации конкретного токена
    @Transactional
    public void invalidateToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}