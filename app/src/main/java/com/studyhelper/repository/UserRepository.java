package com.studyhelper.repository;

import com.studyhelper.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с пользователями.
 */
@Repository
public interface UserRepository extends PagingAndSortingRepository<User, UUID>, CrudRepository<User, UUID> {
    /**
     * Находит пользователя по никнейму.
     *
     * @param nickname никнейм пользователя
     * @return Optional с пользователем, если найден, или пустой Optional
     */
    Optional<User> findByNickname(String nickname);

    /**
     * Проверяет, существует ли пользователь с указанным никнеймом.
     *
     * @param nickname никнейм пользователя
     * @return true, если пользователь существует, иначе false
     */
    boolean existsByNickname(String nickname);

}