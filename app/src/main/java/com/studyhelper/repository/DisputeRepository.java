package com.studyhelper.repository;

import com.studyhelper.entity.Dispute;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;
import java.util.UUID;

public interface DisputeRepository extends CrudRepository<Dispute, UUID> {
    boolean existsByTaskId(UUID taskId);
    Optional<Dispute> findByTaskId(UUID taskId);
}