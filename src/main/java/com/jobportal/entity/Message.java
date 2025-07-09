package com.jobportal.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.jobportal.dto.MessageDTO;
import com.jobportal.dto.MessageStatus;

import java.time.LocalDateTime;

@Document(collection = "messages")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    private Long id;
    private Long senderId;
    private Long receiverId;
    private String text;
    private MessageStatus status = MessageStatus.SENT;
    private String file;
   
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String receiverName;
    private boolean isTyping;
    private boolean senderDeleted = false;
    private boolean receiverDeleted = false;
    private boolean edited;



    public MessageDTO toDTO() {
        return new MessageDTO(this.id, this.senderId, this.receiverId, this.text, this.status ,this.file, this.createdAt, this.updatedAt, this.receiverName,this.isTyping ,this.senderDeleted,this.receiverDeleted, this.edited);
    } 
}