package com.studyhelper.service;

import com.studyhelper.dto.request.ChatMessageRequest;
import com.studyhelper.dto.response.ChatMessageResponse;
import com.studyhelper.dto.response.ChatResponse;
import com.studyhelper.entity.Chat;
import com.studyhelper.entity.ChatMessage;
import com.studyhelper.entity.Task;
import com.studyhelper.entity.TaskStatus;
import com.studyhelper.entity.User;
import com.studyhelper.mapper.ChatMapper;
import com.studyhelper.repository.ChatRepository;
import com.studyhelper.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ChatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final TaskService taskService;
    private final UserRepository userRepository;

    public ChatService(ChatRepository chatRepository, ChatMapper chatMapper, TaskService taskService, UserRepository userRepository) {
        this.chatRepository = chatRepository;
        this.chatMapper = chatMapper;
        this.taskService = taskService;
        this.userRepository = userRepository;
    }

    @Transactional
    public ChatResponse addMessage(UUID taskId, @Valid ChatMessageRequest request, UserDetails currentUser) {
        LOGGER.info("Добавление сообщения в чат для задачи с ID: {} от пользователя: {}", taskId, currentUser.getUsername());

        Task task = taskService.getTaskById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Задача не найдена: " + taskId);
        }
        if (TaskStatus.OPEN.equals(task.getStatus()) || TaskStatus.COMPLETED.equals(task.getStatus())) {
            throw new IllegalStateException("Чат доступен только для задач в статусе TAKEN, PENDING_COMPLETION или DISPUTED");
        }

        User sender = userRepository.findByNickname(currentUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + currentUser.getUsername()));
        if (!task.getAuthorId().equals(sender.getId()) && !task.getExecutorId().equals(sender.getId())) {
            throw new IllegalStateException("Сообщения могут отправлять только автор или исполнитель задачи");
        }

        Chat chat = chatRepository.findByTaskId(taskId)
                .orElseGet(() -> {
                    Chat newChat = new Chat();
                    newChat.setTaskId(taskId);
                    newChat.setAuthorId(task.getAuthorId());
                    newChat.setExecutorId(task.getExecutorId());
                    return chatRepository.save(newChat);
                });

        ChatMessage message = new ChatMessage();
        message.setChat(chat);
        message.setSenderId(sender.getId());
        message.setContent(request.message());
        chat.getMessages().add(message);

        Chat updatedChat = chatRepository.save(chat);
        LOGGER.debug("Сообщение добавлено в чат с ID: {}, taskId: {}", updatedChat.getId(), taskId);

        return chatMapper.toResponse(updatedChat); // senderNickname теперь заполняется в маппере
    }

    @Transactional(readOnly = true)
    public ChatResponse getChatByTaskId(UUID taskId, UserDetails currentUser) {
        LOGGER.info("Получение чата для задачи с ID: {} пользователем: {}", taskId, currentUser.getUsername());

        Task task = taskService.getTaskById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Задача не найдена: " + taskId);
        }

        User user = userRepository.findByNickname(currentUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден: " + currentUser.getUsername()));
        if (!task.getAuthorId().equals(user.getId()) && !task.getExecutorId().equals(user.getId())) {
            throw new IllegalStateException("Чат могут просматривать только автор или исполнитель задачи");
        }

        Chat chat = chatRepository.findByTaskId(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Чат не найден для задачи: " + taskId));

        return chatMapper.toResponse(chat); // senderNickname теперь заполняется в маппере
    }
}