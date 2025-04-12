package com.studyhelper.repository;

import com.studyhelper.entity.Chat;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChatRepository extends CrudRepository<Chat, UUID> {
    Optional<Chat> findByTaskId(UUID taskId);
}