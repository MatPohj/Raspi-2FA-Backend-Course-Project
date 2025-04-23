package com.matpohj.nfc_2fa.controller;

import com.matpohj.nfc_2fa.model.GameStats;
import com.matpohj.nfc_2fa.service.GameStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/game-stats")
public class GameStatsController {
    
    @Autowired
    private GameStatsService gameStatsService;
    
    @GetMapping
    public String showGameStats(Authentication authentication, Model model) {
        if (authentication != null) {
            String username = authentication.getName();
            model.addAttribute("gameStatsList", gameStatsService.getUserGameStats(username));
            model.addAttribute("newGameStats", new GameStats());
        }
        return "game-stats";
    }
    
    @PostMapping
    public String addGameStats(@ModelAttribute GameStats gameStats, Authentication authentication) {
        if (authentication != null) {
            gameStatsService.saveGameStats(gameStats, authentication.getName());
        }
        return "redirect:/game-stats";
    }
    
    @GetMapping("/edit/{id}")
    public String editGameStats(@PathVariable Long id, Model model) {
        gameStatsService.getGameStatsById(id).ifPresent(gameStats -> 
            model.addAttribute("gameStats", gameStats));
        return "edit-game-stats";
    }
    
    @PostMapping("/edit/{id}")
    public String updateGameStats(@PathVariable Long id, @ModelAttribute GameStats gameStats, 
                                Authentication authentication) {
        if (authentication != null) {
            gameStats.setId(id);
            gameStatsService.saveGameStats(gameStats, authentication.getName());
        }
        return "redirect:/game-stats";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteGameStats(@PathVariable Long id) {
        gameStatsService.deleteGameStats(id);
        return "redirect:/game-stats";
    }
}