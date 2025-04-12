package com.studyhelper.repository;

import com.studyhelper.entity.Task;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TaskRepository extends PagingAndSortingRepository<Task, UUID>, CrudRepository<Task, UUID> {
}