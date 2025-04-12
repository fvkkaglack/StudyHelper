package com.studyhelper.repository;

import com.studyhelper.entity.Request;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface RequestRepository extends CrudRepository<Request, UUID> {
    List<Request> findByTaskId(UUID taskId);
}