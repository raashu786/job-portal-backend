package com.jobportal.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.jobportal.dto.MessageDTO;
import com.jobportal.dto.MessageStatus;
import com.jobportal.dto.TypingNotification;
import com.jobportal.entity.Message;
import com.jobportal.exception.JobPortalException;
import com.jobportal.repositary.MessageRepository;
import com.jobportal.utility.Utilities;
import com.jobportal.utility.WebSocketSessionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketSessionManager sessionManager;
    private final Utilities utilities;
    private final Cloudinary cloudinary;

    public MessageServiceImpl(
            MessageRepository messageRepository,
            SimpMessagingTemplate messagingTemplate,
            WebSocketSessionManager sessionManager,
            Utilities utilities,
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret
    ) {
        this.messageRepository = messageRepository;
        this.messagingTemplate = messagingTemplate;
        this.sessionManager = sessionManager;
        this.utilities = utilities;
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    @Override
    public MessageDTO sendMessage(Long senderId, Long receiverId, String text, MultipartFile file) throws JobPortalException {
        String fileUrl = null;

      if (file != null && !file.isEmpty()) {
    try {
        String originalFilename = file.getOriginalFilename();
        String resourceType = "auto";

        if (originalFilename != null && originalFilename.matches("(?i).*\\.(pdf|docx?|xlsx?|pptx?)$")) {
            resourceType = "raw";
        }

        Map<String, Object> uploadParams = ObjectUtils.asMap(
            "resource_type", resourceType,
            "filename", originalFilename,
            "use_filename", true,
            "unique_filename", true,
            "public_id", UUID.randomUUID().toString()
        );


       Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
    "resource_type", resourceType,
    "type", "upload", 
    "use_filename", true,
    "unique_filename", true,
    "filename", originalFilename,
    "public_id", UUID.randomUUID().toString() + (
        originalFilename != null && originalFilename.endsWith(".pdf") ? ".pdf" : ""
    )
));



        fileUrl = (String) uploadResult.get("secure_url");

    } catch (Exception e) {
        e.printStackTrace();
        throw new RuntimeException("File upload failed: " + e.getMessage(), e);
    }
}




        Message message = new Message();
        message.setId(Utilities.getNextSequence("messages"));
        message.setSenderId(senderId);
        message.setReceiverId(receiverId);
        message.setText(text);
        message.setFile(fileUrl);
        message.setCreatedAt(LocalDateTime.now());
        message.setUpdatedAt(LocalDateTime.now());
        message.setStatus(MessageStatus.SENT);

        messageRepository.save(message);

        // Update delivery status if receiver is online
        if (sessionManager.getSessionId(receiverId) != null) {
            message.setStatus(MessageStatus.DELIVERED);
            message.setUpdatedAt(LocalDateTime.now());
            messageRepository.save(message);
        }

        MessageDTO dto = message.toDTO();
        messagingTemplate.convertAndSendToUser(receiverId.toString(), "/queue/messages", dto);
        messagingTemplate.convertAndSendToUser(senderId.toString(), "/queue/messages", dto);

        return dto;
    }

    public void sendTypingNotification(Long senderId, Long receiverId, boolean isTyping) {
        TypingNotification notification = new TypingNotification();
        notification.setSenderId(senderId);
        notification.setReceiverId(receiverId);
        notification.setTyping(isTyping);
        
        messagingTemplate.convertAndSendToUser(
            receiverId.toString(),
            "/queue/typing",
            notification
        );
    }
}
