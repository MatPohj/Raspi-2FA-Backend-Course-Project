package com.matpohj.nfc_2fa.config;

import com.matpohj.nfc_2fa.model.User;
import com.matpohj.nfc_2fa.service.NfcService;
import com.matpohj.nfc_2fa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private NfcService nfcService;
    
    // Hardcoded admin NFC tag ID - you can replace this with your actual tag ID
    private static final String ADMIN_NFC_TAG = "245861573272";
    
    @Override
    public void run(String... args) throws Exception {
        // Create admin user
        User admin = userService.createUser("admin", "adminpassword", Set.of("ADMIN", "USER"));
        
        // Create regular user
        userService.createUser("user", "userpassword", Set.of("USER"));
        
        // Register predefined NFC tag for admin
        try {
            nfcService.registerNfcTag(ADMIN_NFC_TAG, "admin");
            System.out.println("Admin NFC tag registered: " + ADMIN_NFC_TAG);
        } catch (IllegalArgumentException e) {
            // Tag might already exist, just log it
            System.out.println("Note: " + e.getMessage());
        }
    }
}