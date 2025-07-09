package com.jobportal.utility;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Component
public class WebSocketEventListener {

    @Autowired
    private WebSocketSessionManager sessionManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener
    public void handleSessionConnect(SessionConnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String userIdHeader = accessor.getUser() != null ? accessor.getUser().getName() : null;

        if (userIdHeader != null) {
            Long userId = Long.valueOf(userIdHeader);
            sessionManager.registerUser(userId, accessor.getSessionId());
            broadcastOnlineUsers(); // 🔔 Broadcast after connect
        }
    }

    @EventListener
    public void handleSessionDisconnect(SessionDisconnectEvent event) {
        sessionManager.removeUserBySessionId(event.getSessionId());
        broadcastOnlineUsers(); // 🔔 Broadcast after disconnect
    }

    private void broadcastOnlineUsers() {
        Set<Long> onlineUsers = sessionManager.getOnlineUsers();
        messagingTemplate.convertAndSend("/topic/online-users", onlineUsers);
        System.out.println("📢 Broadcasted online users: " + onlineUsers);
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        System.out.println("Subscribed to: " + accessor.getDestination());
    }
}
