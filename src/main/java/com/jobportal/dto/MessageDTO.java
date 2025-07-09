package com.jobportal.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;

import com.jobportal.entity.Message;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDTO {
    @Id
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String text;
    private MessageStatus status;
    private String file;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String receiverName;
    private boolean isTyping;
    private boolean senderDeleted = false;
    private boolean receiverDeleted = false;
    private boolean edited;
    public Message toEntity() {
        return new Message(this.id, this.senderId, this.receiverId, this.text, this.status, this.file, this.createdAt, this.updatedAt, this.receiverName,this.isTyping,this.senderDeleted,this.receiverDeleted, this.edited);
    }
}