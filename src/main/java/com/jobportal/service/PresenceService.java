package com.jobportal.service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class PresenceService {
    private final Set<Long> onlineUsers = ConcurrentHashMap.newKeySet();

    public boolean isUserOnline(Long userId) {
        return onlineUsers.contains(userId);
    }

    public void markUserOnline(Long userId) {
        onlineUsers.add(userId);
    }

    public void markUserOffline(Long userId) {
        onlineUsers.remove(userId);
    }

    public Set<Long> getOnlineUsers() {
        return onlineUsers;
    }
}

