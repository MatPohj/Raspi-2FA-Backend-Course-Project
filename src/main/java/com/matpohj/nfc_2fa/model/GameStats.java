package com.matpohj.nfc_2fa.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "game_stats")
public class GameStats {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private int kills;
    private int assists;
    private int deaths;
    private int durationMinutes;
    private String matchLink;
    private LocalDateTime createdAt;
    private String map;
    private String game;

    public String getMap() {
        return map;
    }

    public void setMap(String map) {
        this.map = map;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    // Calculated field (not stored in DB)
    @Transient
    private double kdRatio;
    
    public GameStats() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public int getKills() {
        return kills;
    }
    
    public void setKills(int kills) {
        this.kills = kills;
    }
    
    public int getAssists() {
        return assists;
    }
    
    public void setAssists(int assists) {
        this.assists = assists;
    }
    
    public int getDeaths() {
        return deaths;
    }
    
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
    
    public int getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public String getMatchLink() {
        return matchLink;
    }
    
    public void setMatchLink(String matchLink) {
        this.matchLink = matchLink;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    // Calculate K/D ratio
    public double getKdRatio() {
        if (deaths == 0) {
            return kills; // Avoid division by zero
        }
        return (double) kills / deaths;
    }
}