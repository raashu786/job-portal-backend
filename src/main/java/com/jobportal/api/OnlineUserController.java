package com.jobportal.api;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jobportal.utility.WebSocketSessionManager;

@RestController
@RequestMapping("/api/online")
public class OnlineUserController {

    @Autowired
    private WebSocketSessionManager sessionManager;

    @GetMapping
 public Set<Long> getOnlineUsers() {
    Set<Long> online = sessionManager.getOnlineUsers();
    System.out.println("🟢 Online users fetched: " + online);
    return online;
}
}