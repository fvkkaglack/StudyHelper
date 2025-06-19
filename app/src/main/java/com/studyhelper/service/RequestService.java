package com.studyhelper.service;

import com.studyhelper.dto.request.RequestRequest;
import com.studyhelper.dto.response.RequestResponse;
import com.studyhelper.dto.response.TaskResponse;
import com.studyhelper.entity.Request;
import com.studyhelper.entity.Task;
import com.studyhelper.entity.TaskStatus;
import com.studyhelper.entity.User;
import com.studyhelper.mapper.RequestMapper;
import com.studyhelper.repository.RequestRepository;
import com.studyhelper.repository.TaskRepository;
import com.studyhelper.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RequestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestService.class);

    private final RequestRepository requestRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    public RequestService(RequestRepository requestRepository, TaskRepository taskRepository,
                          UserRepository userRepository, RequestMapper requestMapper) {
        this.requestRepository = requestRepository;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.requestMapper = requestMapper;
    }

    @Transactional
    public RequestResponse createRequest(@Valid RequestRequest request, UUID taskId, UserDetails currentUser) {
        LOGGER.info("Создание заявки для задачи с ID: {} от пользователя: {}", taskId, currentUser.getUsername());

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена"));

        if (!TaskStatus.OPEN.equals(task.getStatus())) {
            throw new IllegalStateException("Задача не открыта для подачи заявок");
        }

        User user = userRepository.findByNickname(currentUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));

        if (task.getAuthorId().equals(user.getId())) {
            throw new IllegalStateException("Автор задачи не может подать заявку на её выполнение");
        }

        if (requestRepository.findByTaskId(taskId).stream()
                .anyMatch(req -> req.getUserId().equals(user.getId()))) {
            throw new IllegalStateException("Вы уже подали заявку на эту задачу");
        }

        // Используем маппер для создания заявки, передаём Task и User
        Request entity = requestMapper.toRequest(request, task, user);
        Request savedRequest = requestRepository.save(entity);

        LOGGER.debug("Заявка создана с ID: {}, taskId: {}, userId: {}, status: {}, comment: {}",
                savedRequest.getId(), savedRequest.getTask().getId(), savedRequest.getUserId(), savedRequest.getStatus(), savedRequest.getComment());

        return requestMapper.toResponse(savedRequest);
    }

    @Transactional
    public TaskResponse acceptRequest(UUID requestId, UserDetails currentUser) {
        LOGGER.info("Принятие заявки с ID: {} пользователем: {}", requestId, currentUser.getUsername());

        // Находим заявку
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Заявка не найдена"));

        // Получаем задачу из заявки
        Task task = request.getTask();
        UUID taskId = task.getId();

        if (!TaskStatus.OPEN.equals(task.getStatus())) {
            throw new IllegalStateException("Задача не открыта для назначения исполнителя");
        }

        User author = userRepository.findByNickname(currentUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));
        if (!task.getAuthorId().equals(author.getId())) {
            throw new IllegalStateException("Только автор задачи может принимать заявки");
        }

        User executor = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Исполнитель не найден"));

        if (task.getAuthorId().equals(executor.getId())) {
            throw new IllegalStateException("Автор задачи не может быть её исполнителем");
        }

        // Обновляем количество взятых задач исполнителя
        executor.setTasksTaken(executor.getTasksTaken() + 1);
        userRepository.save(executor);

        // Обновляем задачу: назначаем исполнителя и меняем статус
        task.setExecutorId(executor.getId());
        task.setStatus(TaskStatus.TAKEN);
        Task updatedTask = taskRepository.save(task);

        // Удаляем все заявки, связанные с задачей, включая принятую
        requestRepository.deleteByTaskId(taskId);

        LOGGER.debug("Заявка принята, задача с ID: {} теперь в статусе: {}, все заявки удалены", updatedTask.getId(), updatedTask.getStatus());

        return new TaskResponse(
                updatedTask.getId(),
                author.getNickname(),
                updatedTask.getTitle(),
                updatedTask.getDescription(),
                updatedTask.getReward(),
                updatedTask.getDeadline(),
                updatedTask.getStatus(),
                executor.getNickname()
        );
    }
}