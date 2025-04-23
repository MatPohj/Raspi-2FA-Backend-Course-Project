package com.matpohj.nfc_2fa.controller;
import com.matpohj.nfc_2fa.service.NfcService;
import com.matpohj.nfc_2fa.service.VerificationSessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;


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
        String sessionId = request.get("sessionId");
        
        if (tagId == null || username == null) {
            return ResponseEntity.badRequest().body("Missing tagId or username");
        }
        
        boolean valid = nfcService.validateNfcTag(tagId, username);
        
        if (sessionId != null) {
            String expectedUser = verificationSessionService.getUsernameForSession(sessionId);
            if (!username.equals(expectedUser)) {
                valid = false;
            }
            if (valid && expectedUser != null) {
                verificationSessionService.markVerificationComplete(sessionId);
            }
        }
        
        return ResponseEntity.ok(Map.of("valid", valid));
    }
}