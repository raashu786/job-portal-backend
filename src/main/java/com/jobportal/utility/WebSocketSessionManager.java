package com.jobportal.utility;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

@Component
public class WebSocketSessionManager {
    private final Map<Long, String> userSessionMap = new ConcurrentHashMap<>();

    public void registerUser(Long userId, String sessionId) {
    userSessionMap.put(userId, sessionId);
    System.out.println("📦 Current session map: " + userSessionMap);
}


   public void removeUserBySessionId(String sessionId) {
    Long userIdToRemove = null;
    for (Map.Entry<Long, String> entry : userSessionMap.entrySet()) {
        if (entry.getValue().equals(sessionId)) {
            userIdToRemove = entry.getKey();
            break;
        }
    }
    if (userIdToRemove != null) {
        userSessionMap.remove(userIdToRemove);
        System.out.println("🗑️ Removed user " + userIdToRemove + " for session " + sessionId);
    }
}

    public Set<Long> getOnlineUsers() {
        return userSessionMap.keySet();
    }

    public String getSessionId(Long userId) {
        return userSessionMap.get(userId);
    }
}
