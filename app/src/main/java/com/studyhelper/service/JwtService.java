package com.studyhelper.service;

import com.studyhelper.entity.RefreshToken;
import com.studyhelper.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Сервис для работы с JWT-токенами.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private final RefreshTokenService refreshTokenService;

    public JwtService(RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    /**
     * Извлекает имя пользователя из токена.
     *
     * @param token JWT-токен
     * @return имя пользователя
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Извлекает дату истечения срока действия токена.
     *
     * @param token JWT-токен
     * @return дата истечения
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлекает конкретное утверждение (claim) из токена.
     *
     * @param token JWT-токен
     * @param claimsResolver функция для извлечения утверждения
     * @param <T> тип утверждения
     * @return значение утверждения
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Проверяет, действителен ли токен для указанного пользователя.
     *
     * @param token JWT-токен
     * @param userDetails данные пользователя
     * @return true, если токен действителен, иначе false
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Проверяет, действителен ли refresh-токен.
     *
     * @param refreshToken refresh-токен
     * @return true, если токен действителен, иначе false
     */
    public boolean isRefreshTokenValid(String refreshToken) {
        return refreshTokenService.findByTokenOptional(refreshToken)
                .map(refreshTokenService::isRefreshTokenValid)
                .orElse(false);
    }

    /**
     * Генерирует access-токен для пользователя.
     *
     * @param userDetails данные пользователя
     * @return access-токен
     */
    public String generateAccessToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails, accessTokenExpiration);
    }

    /**
     * Генерирует refresh-токен для пользователя и сохраняет его в базе.
     *
     * @param userDetails данные пользователя
     * @return refresh-токен
     */
    public String generateRefreshToken(UserDetails userDetails) {
        String token = generateToken(new HashMap<>(), userDetails, refreshTokenExpiration);
        if (userDetails instanceof User user) {
            // Удаляем старые токены для пользователя
            refreshTokenService.deleteByUser(user);

            // Сохраняем новый refresh-токен
            refreshTokenService.createRefreshToken(user, token);
        }
        return token;
    }

    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
}