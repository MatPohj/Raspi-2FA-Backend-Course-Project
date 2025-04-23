package com.matpohj.nfc_2fa.service;

import com.matpohj.nfc_2fa.model.GameStats;
import com.matpohj.nfc_2fa.model.User;
import com.matpohj.nfc_2fa.repository.GameStatsRepository;
import com.matpohj.nfc_2fa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GameStatsService {
    
    @Autowired
    private GameStatsRepository gameStatsRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public GameStats saveGameStats(GameStats gameStats, String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        
        gameStats.setUser(userOpt.get());
        return gameStatsRepository.save(gameStats);
    }
    
    public List<GameStats> getUserGameStats(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + username);
        }
        
        return gameStatsRepository.findByUserOrderByCreatedAtDesc(userOpt.get());
    }
    
    public Optional<GameStats> getGameStatsById(Long id) {
        return gameStatsRepository.findById(id);
    }
    
    public void deleteGameStats(Long id) {
        gameStatsRepository.deleteById(id);
    }
}