package com.jobportal.api;
import com.jobportal.dto.MessageDTO;
import com.jobportal.dto.MessageStatus;
import com.jobportal.dto.TypingNotification;
import com.jobportal.entity.Message;
import com.jobportal.exception.JobPortalException;
import com.jobportal.jwt.CustomUserDetails;
import com.jobportal.repositary.MessageRepository;
import com.jobportal.service.MessageService;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/messages")
public class MessageAPI {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping(value = "/{receiverId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
public MessageDTO sendMessage(
    @PathVariable Long receiverId,
@RequestParam(required = false) String text, 
    @RequestPart(required = false) MultipartFile file,
    @RequestHeader("X-User-Id") Long senderId
) throws JobPortalException {
    System.out.println("Received file: " + (file != null ? file.getOriginalFilename() : "null"));
    System.out.println("Received text: " + text);
    MessageDTO savedMessage = messageService.sendMessage(senderId, receiverId, text, file);
    messagingTemplate.convertAndSendToUser(receiverId.toString(), "/queue/messages", savedMessage);
    messagingTemplate.convertAndSendToUser(senderId.toString(), "/queue/messages", savedMessage);
   
    return savedMessage;
    
}
    @GetMapping("/{userId}")
        public List<Message> getMessages(
    @PathVariable Long userId,
    @RequestParam Long senderId
) {
    List<Message> messages = messageRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByCreatedAtAsc(
        senderId, userId, senderId, userId
    );
    return messages;
}
@PatchMapping("/{messageId}/delivered")
public ResponseEntity<?> markAsDelivered(@PathVariable Long messageId) {
    Optional<Message> optional = messageRepository.findById(messageId);
    if (optional.isPresent()) {
        Message msg = optional.get();
        msg.setStatus(MessageStatus.DELIVERED);
        msg.setUpdatedAt(LocalDateTime.now());
        messageRepository.save(msg);
        return ResponseEntity.ok(msg.toDTO());
    }
    return ResponseEntity.notFound().build();
}

@PatchMapping("/{messageId}/read")
public ResponseEntity<?> markAsRead(@PathVariable Long messageId) {
    Optional<Message> optional = messageRepository.findById(messageId);
    if (optional.isPresent()) {
        Message msg = optional.get();
        msg.setStatus(MessageStatus.READ);
        msg.setUpdatedAt(LocalDateTime.now());
        messageRepository.save(msg);
        messagingTemplate.convertAndSendToUser(
            String.valueOf(msg.getSenderId()), "/queue/messages", msg.toDTO()
        );
        return ResponseEntity.ok(msg.toDTO());
    }
    return ResponseEntity.notFound().build();
}
@MessageMapping("/typing")
public void receiveTypingNotification(TypingNotification notification, Principal principal) {
    Long senderId = Long.valueOf(principal.getName());
    notification.setSenderId(senderId);
    messagingTemplate.convertAndSendToUser(
        notification.getReceiverId().toString(),
        "/queue/typing",
        notification
    );

   
}
@PatchMapping("/{messageId}/edit")
public ResponseEntity<?> editMessage(@PathVariable Long messageId, @RequestBody String newText) {
    Optional<Message> optional = messageRepository.findById(messageId);
    if (optional.isPresent()) {
        Message msg = optional.get();
        msg.setText(newText);
        msg.setEdited(true);
        msg.setUpdatedAt(LocalDateTime.now());
        messageRepository.save(msg);

        messagingTemplate.convertAndSendToUser(
            String.valueOf(msg.getReceiverId()), "/queue/messages", msg.toDTO()
        );
        messagingTemplate.convertAndSendToUser(
            String.valueOf(msg.getSenderId()), "/queue/messages", msg.toDTO()
        );
        return ResponseEntity.ok(msg.toDTO());
    } 
    return ResponseEntity.notFound().build();
}

@DeleteMapping("/{messageId}/me")
public ResponseEntity<?> deleteMessageForMe(@PathVariable Long messageId,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
    if (userDetails == null) {
        return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body("Unauthorized");
    }

    Long senderId = userDetails.getProfileId(); // ✅ correct way

    Optional<Message> optional = messageRepository.findById(messageId);
    if (optional.isPresent()) {
        Message msg = optional.get();
        if (msg.getSenderId().equals(senderId)) {
            msg.setSenderDeleted(true);
        } else if (msg.getReceiverId().equals(senderId)) {
            msg.setReceiverDeleted(true);
        } else {
            return ResponseEntity.status(HttpStatus.SC_FORBIDDEN).body("Access denied");
        }
        messageRepository.save(msg);
        return ResponseEntity.ok().build();
    }

    return ResponseEntity.notFound().build();
}



@DeleteMapping("/{messageId}/everyone")
public ResponseEntity<?> deleteMessageForEveryone(@PathVariable Long messageId) {
    Optional<Message> optional = messageRepository.findById(messageId);
    if (optional.isPresent()) {
        Message msg = optional.get();
        msg.setText("🚫This message was deleted");
        msg.setFile(null);
        msg.setUpdatedAt(LocalDateTime.now());
        messageRepository.save(msg);
        messagingTemplate.convertAndSendToUser(
            String.valueOf(msg.getReceiverId()), "/queue/messages", msg.toDTO()
        );
        messagingTemplate.convertAndSendToUser(
            String.valueOf(msg.getSenderId()), "/queue/messages", msg.toDTO()
        );
        return ResponseEntity.ok(msg.toDTO());
    }
    return ResponseEntity.notFound().build();
}


}
