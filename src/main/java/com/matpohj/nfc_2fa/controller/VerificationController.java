package com.matpohj.nfc_2fa.controller;

import com.matpohj.nfc_2fa.service.VerificationSessionService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class VerificationController {
    
    @Autowired
    private VerificationSessionService verificationSessionService;
    
    @GetMapping("/nfc-verification")
    public String nfcVerificationPage(Authentication authentication, Model model, HttpSession httpSession) {
        if (authentication != null) {
            String username = authentication.getName();
            model.addAttribute("username", username);

            // Always create a new session ID for fresh verification
            String sessionId = verificationSessionService.createSession(username);
            httpSession.setAttribute("nfcSessionId", sessionId);
            System.out.println("Created session ID: " + sessionId + " for user: " + username);
            model.addAttribute("sessionId", sessionId);
        } else {
            System.out.println("Warning: No authentication found for NFC verification");
        }
        return "nfc-verification";
    }
    
    @PostMapping("/verify-nfc")
    public String verifyNfc(@RequestParam("verified") boolean verified, HttpSession session) {
        if (verified) {
            session.setAttribute("nfcVerified", true);
            return "redirect:/";
        }
        return "redirect:/nfc-verification?error";
    }
    
    @GetMapping("/api/verification/status")
    @ResponseBody
    public ResponseEntity<?> checkVerificationStatus(@RequestParam("sessionId") String sessionId) {
        boolean completed = verificationSessionService.isVerificationComplete(sessionId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("completed", completed);
        
        if (completed) {
            verificationSessionService.cleanup(sessionId);
        }
        
        return ResponseEntity.ok(response);
    }
}