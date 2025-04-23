package com.matpohj.nfc_2fa.config;

import com.matpohj.nfc_2fa.model.User;
import com.matpohj.nfc_2fa.service.NfcService;
import com.matpohj.nfc_2fa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private NfcService nfcService;

    @Value("${app.security.admin-nfc-tag}")
    private String adminNfcTag;
    
    @Override
    public void run(String... args) throws Exception {
        // Create admin user
        User admin = userService.createUser("admin", "adminpassword", Set.of("ADMIN", "USER"));
        
        // Create regular user
        userService.createUser("user", "userpassword", Set.of("USER"));
        
        // Register predefined NFC tag for admin
        try {
            nfcService.registerNfcTag(adminNfcTag, "admin");
            System.out.println("Admin NFC tag registered successfully");
        } catch (IllegalArgumentException e) {
            // Tag might already exist, just log it
            System.out.println("Note: " + e.getMessage());
        }
    }
}