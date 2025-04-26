package com.matpohj.nfc_2fa.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationSessionService {

    private final Map<String, String> pendingSessions = new ConcurrentHashMap<>();

    private final Map<String, Boolean> completedVerifications = new ConcurrentHashMap<>();

    public String createSession(String username) {
        String sessionId = UUID.randomUUID().toString();
        pendingSessions.put(sessionId, username);
        return sessionId;
    }

    public String getUsernameForSession(String sessionId) {
        return pendingSessions.get(sessionId);
    }

    public void markVerificationComplete(String sessionId) {
        if (pendingSessions.containsKey(sessionId)) {
            completedVerifications.put(sessionId, true);
        }
    }

    public boolean isVerificationComplete(String sessionId) {
        return completedVerifications.getOrDefault(sessionId, false);
    }

    public void cleanup(String sessionId) {
        pendingSessions.remove(sessionId);
        completedVerifications.remove(sessionId);
    }
}