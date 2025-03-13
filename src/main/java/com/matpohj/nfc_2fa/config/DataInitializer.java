package com.matpohj.nfc_2fa.config;

import com.matpohj.nfc_2fa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserService userService;
    
    @Override
    public void run(String... args) throws Exception {
        // Create admin user
        userService.createUser("admin", "adminpassword", Set.of("ADMIN", "USER"));
        
        // Create regular user
        userService.createUser("user", "userpassword", Set.of("USER"));
    }
}