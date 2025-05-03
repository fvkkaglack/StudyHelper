package com.studyhelper.repository;

import com.studyhelper.entity.RefreshToken;
import com.studyhelper.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, UUID> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    // Новый метод для удаления конкретного токена
    void deleteByToken(String token);
}