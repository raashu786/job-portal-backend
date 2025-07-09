package com.jobportal.utility;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

public class CustomHandshakeHandler extends DefaultHandshakeHandler {

   @Override
protected Principal determineUser(ServerHttpRequest request,
                                  WebSocketHandler wsHandler,
                                  Map<String, Object> attributes) {
    String userId = (String) attributes.get("userId");
    System.out.println("✅ [CustomHandshakeHandler] Principal set to: " + userId);
    return userId != null ? new StompPrincipal(userId) : null;
}
}
