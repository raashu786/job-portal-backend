package com.jobportal.repositary;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jobportal.entity.User;

import java.util.Optional;

public interface UserRepositary extends MongoRepository<User , Long> {
    public Optional<User> findByEmail(String email);
    public Optional<User> findByMobile(String mobile);
    boolean existsByMobile(String mobile);
    boolean existsByEmail(String email);
}

