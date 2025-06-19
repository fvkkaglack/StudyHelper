package com.studyhelper.repository;

import com.studyhelper.entity.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, UUID>, CrudRepository<User, UUID> {
    Optional<User> findByNickname(String nickname);
    boolean existsByNickname(String nickname);

}