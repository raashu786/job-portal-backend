package com.jobportal.service;

import com.jobportal.dto.MessageDTO;
import com.jobportal.exception.JobPortalException;

import org.springframework.web.multipart.MultipartFile;

public interface MessageService {
    MessageDTO sendMessage(Long senderId, Long receiverId, String text, MultipartFile image) throws JobPortalException;
}
