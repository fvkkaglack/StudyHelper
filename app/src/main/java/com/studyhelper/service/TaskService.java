package com.studyhelper.service;

import com.studyhelper.dto.request.TaskRequest;
import com.studyhelper.dto.response.PageDto;
import com.studyhelper.dto.response.TaskResponse;
import com.studyhelper.entity.Task;
import com.studyhelper.entity.TaskStatus;
import com.studyhelper.mapper.TaskMapper;
import com.studyhelper.repository.TaskRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

@Service
public class TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final TaskMapper taskMapper;
    private final RequestService requestService;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper, RequestService requestService) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.requestService = requestService;
    }

    public TaskResponse createTask(@Valid TaskRequest request) {
        Task task = taskMapper.toTask(request);
        task.setId(UUID.randomUUID());
        Task savedTask = taskRepository.save(task);
        return taskMapper.toResponse(savedTask);
    }

    public TaskResponse updateTask(UUID id, @Valid TaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        taskMapper.updateTask(task, request);
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toResponse(updatedTask);
    }

    public void deleteTask(UUID id) {
        taskRepository.deleteById(id);
    }

    public PageDto<TaskResponse> getTaskPage(Pageable pageable) {
        Page<Task> page = taskRepository.findAll(pageable);
        return new PageDto<>(
                page.getContent().stream().map(taskMapper::toResponse).toList(),
                page.getNumber(),
                page.getNumberOfElements(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }

    public List<TaskResponse> getTaskList() {
        return StreamSupport.stream(taskRepository.findAll().spliterator(), false)
                .map(taskMapper::toResponse)
                .toList();
    }

    public TaskResponse assignExecutor(UUID taskId, UUID executorId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));
        if (!TaskStatus.OPEN.equals(task.getStatus())) {
            throw new IllegalStateException("Task is not open for assignment");
        }
        task.setExecutorId(executorId);
        task.setStatus(TaskStatus.TAKEN);
        Task updatedTask = taskRepository.save(task);
        return taskMapper.toResponse(updatedTask);
    }
}