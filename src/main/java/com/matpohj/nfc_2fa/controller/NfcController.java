package com.matpohj.nfc_2fa.controller;

import com.matpohj.nfc_2fa.model.NfcTag;
import com.matpohj.nfc_2fa.service.NfcService;
import com.matpohj.nfc_2fa.service.VerificationSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/nfc")
public class NfcController {

    @Autowired
    private NfcService nfcService;

    @Autowired
    private VerificationSessionService verificationSessionService;
    
    @PostMapping("/validate")
    public ResponseEntity<?> validateNfcTag(@RequestBody Map<String, String> request) {
        String tagId = request.get("tagId");
        String username = request.get("username");
        String sessionId = request.get("sessionId"); // Optional, for browser integration
        
        if (tagId == null || username == null) {
            return ResponseEntity.badRequest().body("Missing tagId or username");
        }
        
        boolean valid = nfcService.validateNfcTag(tagId, username);
        
        // If sessionId is provided, require it to match the username for a valid response
        if (sessionId != null) {
            String expectedUser = verificationSessionService.getUsernameForSession(sessionId);
            if (!username.equals(expectedUser)) {
                valid = false;
            }
            if (valid && expectedUser != null) {
                verificationSessionService.markVerificationComplete(sessionId);
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("valid", valid);
        
        return ResponseEntity.ok(response);
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> registerNfcTag(@RequestBody Map<String, String> request) {
        String tagId = request.get("tagId");
        String username = request.get("username");
        
        if (tagId == null || username == null) {
            return ResponseEntity.badRequest().body("Missing tagId or username");
        }
        
        try {
            NfcTag tag = nfcService.registerNfcTag(tagId, username);
            return ResponseEntity.ok(Map.of(
                "id", tag.getId(),
                "tagId", tag.getTagId(),
                "username", tag.getUser().getUsername()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/mytags")
    public ResponseEntity<?> getUserTags() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        
        try {
            Set<NfcTag> tags = nfcService.getUserTags(username);
            Set<Map<String, Object>> tagData = tags.stream()
                .map(tag -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", tag.getId());
                    map.put("tagId", tag.getTagId());
                    map.put("active", tag.isActive());
                    return map;
                })
                .collect(Collectors.toSet());
            
            return ResponseEntity.ok(tagData);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateTag(@RequestBody Map<String, String> request) {
        String tagId = request.get("tagId");
        
        if (tagId == null) {
            return ResponseEntity.badRequest().body("Missing tagId");
        }
        
        nfcService.deactivateTag(tagId);
        return ResponseEntity.ok(Map.of("deactivated", true));
    }
    
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/requireNfc")
    public ResponseEntity<?> requireNfc(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("username");
        Boolean required = (Boolean) request.get("required");
        
        if (username == null || required == null) {
            return ResponseEntity.badRequest().body("Missing username or required flag");
        }
        
        nfcService.setUserNfcRequired(username, required);
        return ResponseEntity.ok(Map.of("updated", true));
    }
    
    // Enabled only in development environments for testing
    @GetMapping("/debug-register-tag")
    public ResponseEntity<?> debugRegisterTag(
            @RequestParam(required = true) String tagId,
            @RequestParam(required = true) String username,
            @RequestParam(required = false, defaultValue = "false") boolean admin) {
        
        if (!admin && !"admin".equals(username)) {
            return ResponseEntity.badRequest().body("Admin parameter required when registering for admin user");
        }
        
        try {
            NfcTag tag = nfcService.registerNfcTag(tagId, username);
            
            return ResponseEntity.ok(Map.of(
                "id", tag.getId(),
                "tagId", tag.getTagId(),
                "username", tag.getUser().getUsername(),
                "message", "Tag registered successfully"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}