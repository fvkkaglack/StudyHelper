/*package com.studyhelper.repository;

import com.studyhelper.entity.ChatMessage;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends CrudRepository<ChatMessage, UUID> {
    List<ChatMessage> findByTaskIdOrderBySentAtAsc(UUID taskId);
    void deleteByTaskId(UUID taskId);
}

 */