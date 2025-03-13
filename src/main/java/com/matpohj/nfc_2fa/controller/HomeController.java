package com.matpohj.nfc_2fa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String homepage(Model model) {
        model.addAttribute("title", "NFC 2FA Application");
        model.addAttribute("message", "Welcome to the NFC 2FA application");
        return "home";
    }
}