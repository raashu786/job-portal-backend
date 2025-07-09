package com.jobportal.repositary;
import com.jobportal.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, Long> {
    List<Message> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAtAsc(
        Long senderId1, Long receiverId1, Long senderId2, Long receiverId2
    );
}