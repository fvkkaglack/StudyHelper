package com.studyhelper.service;

import com.studyhelper.dto.request.TaskRequest;
import com.studyhelper.dto.response.PageDto;
import com.studyhelper.dto.response.TaskResponse;
import com.studyhelper.entity.Task;
import com.studyhelper.entity.TaskStatus;
import com.studyhelper.entity.User;
import com.studyhelper.mapper.TaskMapper;
//import com.studyhelper.repository.ChatMessageRepository;
import com.studyhelper.repository.RequestRepository;
import com.studyhelper.repository.TaskRepository;
import com.studyhelper.repository.UserRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    //private final ChatMessageRepository chatMessageRepository;
    private final TaskMapper taskMapper;
    private final DisputeService disputeService;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository,
                       RequestRepository requestRepository, //ChatMessageRepository chatMessageRepository,
                       TaskMapper taskMapper, DisputeService disputeService) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.requestRepository = requestRepository;
        //this.chatMessageRepository = chatMessageRepository;
        this.taskMapper = taskMapper;
        this.disputeService = disputeService;
    }

    public Task getTaskById(UUID taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена: " + taskId));
    }

    @Transactional
    public TaskResponse createTask(@Valid TaskRequest request, UserDetails currentUser) {
        LOGGER.info("Создание задачи для пользователя: {}", currentUser.getUsername());

        User author = userRepository.findByNickname(currentUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));

        author.setTasksCreated(author.getTasksCreated() + 1);
        userRepository.save(author);

        Task task = taskMapper.toTask(request, author);
        Task savedTask = taskRepository.save(task);

        LOGGER.debug("Создана задача с ID: {}, executorId: {}, status: {}", savedTask.getId(), savedTask.getExecutorId(), savedTask.getStatus());

        return new TaskResponse(
                savedTask.getId(),
                author.getNickname(),
                savedTask.getTitle(),
                savedTask.getDescription(),
                savedTask.getReward(),
                savedTask.getDeadline(),
                savedTask.getStatus(),
                null
        );
    }

    @Transactional
    public void deleteTask(UUID id) {
        if (!taskRepository.existsById(id)) {
            throw new IllegalArgumentException("Задача не найдена");
        }
        requestRepository.deleteByTaskId(id);
        //chatMessageRepository.deleteByTaskId(id); // Удаляем сообщения чата
        taskRepository.deleteById(id);
    }

    public PageDto<TaskResponse> getTaskPage(Pageable pageable) {
        Page<Task> page = taskRepository.findAll(pageable);
        List<TaskResponse> responses = page.getContent().stream()
                .map(task -> {
                    User author = userRepository.findById(task.getAuthorId())
                            .orElseThrow(() -> new IllegalStateException("Автор не найден"));
                    User executor = task.getExecutorId() != null ?
                            userRepository.findById(task.getExecutorId())
                                    .orElseThrow(() -> new IllegalStateException("Исполнитель не найден")) : null;
                    TaskResponse response = taskMapper.toResponse(task);
                    return new TaskResponse(
                            response.id(),
                            author.getNickname(),
                            response.title(),
                            response.description(),
                            response.reward(),
                            response.deadline(),
                            response.status(),
                            executor != null ? executor.getNickname() : null
                    );
                })
                .collect(Collectors.toList());

        return new PageDto<>(
                responses,
                page.getNumber(),
                page.getNumberOfElements(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public List<TaskResponse> getTaskList() {
        return StreamSupport.stream(taskRepository.findAll().spliterator(), false)
                .map(task -> {
                    User author = userRepository.findById(task.getAuthorId())
                            .orElseThrow(() -> new IllegalStateException("Автор не найден"));
                    User executor = task.getExecutorId() != null ?
                            userRepository.findById(task.getExecutorId())
                                    .orElseThrow(() -> new IllegalStateException("Исполнитель не найден")) : null;
                    TaskResponse response = taskMapper.toResponse(task);
                    return new TaskResponse(
                            response.id(),
                            author.getNickname(),
                            response.title(),
                            response.description(),
                            response.reward(),
                            response.deadline(),
                            response.status(),
                            executor != null ? executor.getNickname() : null
                    );
                })
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getOpenTasks() {
        return StreamSupport.stream(taskRepository.findAll().spliterator(), false)
                .filter(task -> TaskStatus.OPEN.equals(task.getStatus()))
                .map(task -> {
                    User author = userRepository.findById(task.getAuthorId())
                            .orElseThrow(() -> new IllegalStateException("Автор не найден"));
                    TaskResponse response = taskMapper.toResponse(task);
                    return new TaskResponse(
                            response.id(),
                            author.getNickname(),
                            response.title(),
                            response.description(),
                            response.reward(),
                            response.deadline(),
                            response.status(),
                            null
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse markTaskCompleted(UUID taskId, UserDetails currentUser) {
        LOGGER.info("Исполнитель помечает задачу с ID: {} как выполненную", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена"));

        if (!TaskStatus.TAKEN.equals(task.getStatus())) {
            throw new IllegalStateException("Задача должна быть в статусе TAKEN для отметки о выполнении");
        }

        User executor = userRepository.findByNickname(currentUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));
        if (!executor.getId().equals(task.getExecutorId())) {
            throw new IllegalStateException("Только исполнитель может пометить задачу как выполненную");
        }

        task.setStatus(TaskStatus.PENDING_COMPLETION);
        Task updatedTask = taskRepository.save(task);

        User author = userRepository.findById(updatedTask.getAuthorId())
                .orElseThrow(() -> new IllegalStateException("Автор не найден"));

        TaskResponse response = taskMapper.toResponse(updatedTask);
        return new TaskResponse(
                response.id(),
                author.getNickname(),
                response.title(),
                response.description(),
                response.reward(),
                response.deadline(),
                response.status(),
                executor.getNickname()
        );
    }

    @Transactional
    public TaskResponse confirmTaskCompletion(UUID taskId, UserDetails currentUser) {
        LOGGER.info("Автор подтверждает выполнение задачи с ID: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена"));

        if (!TaskStatus.PENDING_COMPLETION.equals(task.getStatus())) {
            throw new IllegalStateException("Задача должна быть в статусе PENDING_COMPLETION для подтверждения");
        }

        User author = userRepository.findByNickname(currentUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));
        if (!task.getAuthorId().equals(author.getId())) {
            throw new IllegalStateException("Только автор может подтвердить выполнение задачи");
        }

        task.setStatus(TaskStatus.COMPLETED);
        Task updatedTask = taskRepository.save(task);

        User executor = userRepository.findById(updatedTask.getExecutorId())
                .orElseThrow(() -> new IllegalStateException("Исполнитель не найден"));

        TaskResponse response = taskMapper.toResponse(updatedTask);
        return new TaskResponse(
                response.id(),
                author.getNickname(),
                response.title(),
                response.description(),
                response.reward(),
                response.deadline(),
                response.status(),
                executor.getNickname()
        );
    }

    @Transactional
    public TaskResponse rejectTaskCompletion(UUID taskId, UserDetails currentUser, String disputeReason) {
        LOGGER.info("Автор отклоняет выполнение задачи с ID: {}", taskId);

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена"));

        if (!TaskStatus.PENDING_COMPLETION.equals(task.getStatus())) {
            throw new IllegalStateException("Задача должна быть в статусе PENDING_COMPLETION для отклонения");
        }

        User author = userRepository.findByNickname(currentUser.getUsername())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));
        if (!task.getAuthorId().equals(author.getId())) {
            throw new IllegalStateException("Только автор может отклонить выполнение задачи");
        }

        if (task.getExecutorId() == null) {
            throw new IllegalStateException("У задачи нет исполнителя для отклонения");
        }

        if (disputeReason == null || disputeReason.trim().isEmpty()) {
            throw new IllegalArgumentException("Причина спора не может быть пустой");
        }

        task.setStatus(TaskStatus.DISPUTED);
        Task updatedTask = taskRepository.save(task);

        User executor = userRepository.findById(updatedTask.getExecutorId())
                .orElseThrow(() -> new IllegalStateException("Исполнитель не найден"));

        // Создаём спор от имени автора
        disputeService.createDisputeForTaskRejection(updatedTask, author, disputeReason);

        // Уведомляем исполнителя о возможности создать спор (заглушка для уведомления)
        LOGGER.info("Уведомление исполнителя {}: Задача с ID {} была отклонена. Вы можете создать спор через /api/v1/dispute/task/{}",
                executor.getNickname(), taskId, taskId);

        TaskResponse response = taskMapper.toResponse(updatedTask);
        return new TaskResponse(
                response.id(),
                author.getNickname(),
                response.title(),
                response.description(),
                response.reward(),
                response.deadline(),
                response.status(),
                executor.getNickname()
        );
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkDeadlines() {
        LOGGER.debug("Проверка дедлайнов задач");

        Iterable<Task> tasks = taskRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (Task task : tasks) {
            if (task.getDeadline() == null || now.isBefore(task.getDeadline())) {
                continue;
            }

            LOGGER.info("Удаление задачи с ID: {} и связанных заявок из-за просроченного дедлайна (статус: {})", task.getId(), task.getStatus());
            User author = userRepository.findById(task.getAuthorId())
                    .orElseThrow(() -> new IllegalStateException("Автор не найден"));
            author.setTasksCreated(author.getTasksCreated() - 1);
            userRepository.save(author);

            if (task.getExecutorId() != null) {
                User executor = userRepository.findById(task.getExecutorId())
                        .orElseThrow(() -> new IllegalStateException("Исполнитель не найден"));
                executor.setTasksTaken(executor.getTasksTaken() - 1);
                userRepository.save(executor);
            }

            requestRepository.deleteByTaskId(task.getId());
            //chatMessageRepository.deleteByTaskId(task.getId()); // Удаляем сообщения чата
            taskRepository.delete(task);
        }
    }
}