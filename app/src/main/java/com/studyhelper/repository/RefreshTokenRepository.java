package com.studyhelper.repository;

import com.studyhelper.entity.RefreshToken;
import com.studyhelper.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с refresh-токенами.
 */
@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {
    /**
     * Находит refresh-токен по его значению.
     *
     * @param token значение refresh-токена
     * @return Optional с refresh-токеном, если найден, или пустой Optional
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Удаляет все refresh-токены, связанные с пользователем.
     *
     * @param user пользователь
     */
    void deleteByUser(User user);
}