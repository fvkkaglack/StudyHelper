package com.studyhelper.service;

import com.studyhelper.dto.request.ChatMessageRequest;
import com.studyhelper.dto.response.ChatResponse;
import com.studyhelper.entity.Chat;
import com.studyhelper.mapper.ChatMapper;
import com.studyhelper.repository.ChatRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ChatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final TaskService taskService;

    public ChatService(ChatRepository chatRepository, ChatMapper chatMapper, TaskService taskService) {
        this.chatRepository = chatRepository;
        this.chatMapper = chatMapper;
        this.taskService = taskService;
    }

    public ChatResponse addMessage(UUID taskId, @Valid ChatMessageRequest request) {
        Chat chat = chatRepository.findByTaskId(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found for task: " + taskId));
        chat.setMessages(chat.getMessages() + "\n" + request.message());
        chatRepository.save(chat);
        return chatMapper.toResponse(chat);
    }

    public ChatResponse getChatByTaskId(UUID taskId) {
        Chat chat = chatRepository.findByTaskId(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found for task: " + taskId));
        return chatMapper.toResponse(chat);
    }
}