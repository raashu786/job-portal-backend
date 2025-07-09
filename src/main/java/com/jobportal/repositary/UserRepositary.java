package com.jobportal.repositary;

import com.jobportal.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface UserRepositary extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);
    Optional<User> findByMobile(String mobile);
    boolean existsByMobile(String mobile);
    boolean existsByEmail(String email);
    Long findIdByEmail(String email);

}