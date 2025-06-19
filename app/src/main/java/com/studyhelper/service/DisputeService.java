package com.studyhelper.service;

import com.studyhelper.dto.request.DisputeRequest;
import com.studyhelper.dto.response.DisputeResponse;
import com.studyhelper.entity.Dispute;
import com.studyhelper.entity.Task;
import com.studyhelper.entity.TaskStatus;
import com.studyhelper.entity.User;
import com.studyhelper.mapper.DisputeMapper;
import com.studyhelper.repository.DisputeRepository;
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
public class DisputeService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DisputeService.class);

    private final DisputeRepository disputeRepository;
    private final DisputeMapper disputeMapper;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public DisputeService(DisputeRepository disputeRepository, DisputeMapper disputeMapper,
                          TaskRepository taskRepository, UserRepository userRepository) {
        this.disputeRepository = disputeRepository;
        this.disputeMapper = disputeMapper;
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public DisputeResponse createDispute(UUID taskId, @Valid DisputeRequest request, UserDetails currentUser) {
        LOGGER.info("Создание спора для задачи с ID: {} от пользователя: {}", taskId, currentUser.getUsername());

        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Задача не найдена"));

        if (!TaskStatus.DISPUTED.equals(task.getStatus())) {
            throw new IllegalStateException("Спор можно создать только для задачи в статусе DISPUTED");
        }

        User complainant = userRepository.findByNickname(currentUser.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        if (!task.getAuthorId().equals(complainant.getId()) && !task.getExecutorId().equals(complainant.getId())) {
            throw new IllegalStateException("Спор может создать только автор или исполнитель задачи");
        }

        if (disputeRepository.existsByTaskId(taskId)) {
            throw new IllegalStateException("Спор для этой задачи уже существует");
        }

        Dispute dispute = disputeMapper.toDispute(request, task, complainant);
        Dispute savedDispute = disputeRepository.save(dispute);

        LOGGER.debug("Спор создан с ID: {}, taskId: {}, complainantId: {}, status: PENDING",
                savedDispute.getId(), savedDispute.getTaskId(), savedDispute.getComplainantId());

        return disputeMapper.toResponse(savedDispute);
    }

    @Transactional
    public DisputeResponse createDisputeForTaskRejection(Task task, User complainant, String reason) {
        LOGGER.info("Создание спора для отклонения задачи с ID: {} от пользователя: {}", task.getId(), complainant.getId());

        if (!TaskStatus.DISPUTED.equals(task.getStatus())) {
            throw new IllegalStateException("Спор можно создать только для задачи в статусе DISPUTED");
        }

        if (!task.getAuthorId().equals(complainant.getId()) && !task.getExecutorId().equals(complainant.getId())) {
            throw new IllegalStateException("Спор может создать только автор или исполнитель задачи");
        }

        if (disputeRepository.existsByTaskId(task.getId())) {
            throw new IllegalStateException("Спор для этой задачи уже существует");
        }

        DisputeRequest request = new DisputeRequest(reason);
        Dispute dispute = disputeMapper.toDispute(request, task, complainant);
        Dispute savedDispute = disputeRepository.save(dispute);

        LOGGER.debug("Спор создан с ID: {}, taskId: {}, complainantId: {}, status: PENDING",
                savedDispute.getId(), savedDispute.getTaskId(), savedDispute.getComplainantId());

        return disputeMapper.toResponse(savedDispute);
    }

    @Transactional
    public DisputeResponse resolveDispute(UUID id, String resolution) {
        LOGGER.info("Разрешение спора с ID: {}", id);

        Dispute dispute = disputeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Спор не найден"));

        dispute.setStatus(Dispute.DisputeStatus.RESOLVED);
        dispute.setResolution(resolution);
        Dispute savedDispute = disputeRepository.save(dispute);

        LOGGER.debug("Спор с ID: {} разрешён с решением: {}", id, resolution);

        return disputeMapper.toResponse(savedDispute);
    }
}