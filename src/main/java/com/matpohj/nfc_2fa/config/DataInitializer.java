package com.matpohj.nfc_2fa.config;

import com.matpohj.nfc_2fa.model.GameStats;
import com.matpohj.nfc_2fa.model.User;
import com.matpohj.nfc_2fa.repository.GameStatsRepository;
import com.matpohj.nfc_2fa.repository.UserRepository;
import com.matpohj.nfc_2fa.service.NfcService;
import com.matpohj.nfc_2fa.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Autowired
    private NfcService nfcService;

    @Autowired
    private GameStatsRepository gameStatsRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.security.admin-nfc-tag}")
    private String adminNfcTag;

    @Value("${app.admin.password:adminpassword}")
    private String adminPassword;

    @Value("${app.user.password:userpassword}")
    private String userPassword;

    @Override
    public void run(String... args) throws Exception {
        // Check if admin user exists first
        Optional<User> existingAdmin = userRepository.findByUsername("admin");
        User admin;
        if (existingAdmin.isEmpty()) {
            admin = userService.createUser("admin", adminPassword, Set.of("ADMIN", "USER"));
            System.out.println("Created admin user");
        } else {
            admin = existingAdmin.get();
            System.out.println("Admin user already exists");
        }
        
        // Check if regular user exists first
        Optional<User> existingUser = userRepository.findByUsername("user");
        User regularUser;
        if (existingUser.isEmpty()) {
            regularUser = userService.createUser("user", userPassword, Set.of("USER"));
            System.out.println("Created regular user");
        } else {
            regularUser = existingUser.get();
            System.out.println("Regular user already exists");
        }
        
        // Register predefined NFC tag for admin
        try {
            nfcService.registerNfcTag(adminNfcTag, "admin");
            System.out.println("Admin NFC tag registered successfully");
        } catch (IllegalArgumentException e) {
            // Tag might already exist, just log it
            System.out.println("Note: " + e.getMessage());
        }

        // Create test game stats
        createTestGameStats(admin);
        createTestGameStats(regularUser);
    }

    private void createTestGameStats(User user) {
        // Only create test data if no stats exist for this user
        if (!gameStatsRepository.findByUserOrderByCreatedAtDesc(user).isEmpty()) {
            return;
        }

        boolean isAdmin = user.getRoles().contains("ADMIN");
        System.out.println("Creating test data for " + (isAdmin ? "admin" : "regular user"));

        if (isAdmin) {
            // Admin gets different matches

            // Amazing match
            GameStats match1 = new GameStats();
            match1.setUser(user);
            match1.setKills(42);
            match1.setDeaths(7);
            match1.setAssists(12);
            match1.setDurationMinutes(45);
            match1.setMatchLink("https://steam-profiles.com/match/admin-12345");
            match1.setCreatedAt(LocalDateTime.now().minusDays(1));
            match1.setMap("Ancient");
            match1.setGame("CS2");
            gameStatsRepository.save(match1);

            // Very good match
            GameStats match2 = new GameStats();
            match2.setUser(user);
            match2.setKills(28);
            match2.setDeaths(10);
            match2.setAssists(8);
            match2.setDurationMinutes(38);
            match2.setCreatedAt(LocalDateTime.now().minusDays(2));
            match2.setMap("Vertigo");
            match2.setGame("CS2");
            gameStatsRepository.save(match2);

            // Different game
            GameStats match3 = new GameStats();
            match3.setUser(user);
            match3.setKills(20);
            match3.setDeaths(15);
            match3.setAssists(10);
            match3.setDurationMinutes(35);
            match3.setMatchLink("https://valorant-stats.com/match/98765");
            match3.setCreatedAt(LocalDateTime.now().minusDays(4));
            match3.setMap("Split");
            match3.setGame("Valorant");
            gameStatsRepository.save(match3);

            // Older match
            GameStats match4 = new GameStats();
            match4.setUser(user);
            match4.setKills(35);
            match4.setDeaths(14);
            match4.setAssists(6);
            match4.setDurationMinutes(55);
            match4.setCreatedAt(LocalDateTime.now().minusWeeks(2));
            match4.setMap("Overpass");
            match4.setGame("CS2");
            gameStatsRepository.save(match4);
        } else {
            // Regular user

            // Good match
            GameStats match1 = new GameStats();
            match1.setUser(user);
            match1.setKills(24);
            match1.setDeaths(14);
            match1.setAssists(7);
            match1.setDurationMinutes(45);
            match1.setMatchLink("https://steam-profiles.com/match/12345");
            match1.setCreatedAt(LocalDateTime.now().minusDays(1));
            match1.setMap("Mirage");
            match1.setGame("CS2");
            gameStatsRepository.save(match1);

            // Average match
            GameStats match2 = new GameStats();
            match2.setUser(user);
            match2.setKills(15);
            match2.setDeaths(17);
            match2.setAssists(4);
            match2.setDurationMinutes(38);
            match2.setCreatedAt(LocalDateTime.now().minusDays(3));
            match2.setMap("Dust II");
            match2.setGame("CS2");
            gameStatsRepository.save(match2);

            // Bad match
            GameStats match3 = new GameStats();
            match3.setUser(user);
            match3.setKills(8);
            match3.setDeaths(19);
            match3.setAssists(3);
            match3.setDurationMinutes(32);
            match3.setMatchLink("https://steam-profiles.com/match/67890");
            match3.setCreatedAt(LocalDateTime.now().minusDays(5));
            match3.setMap("Inferno");
            match3.setGame("CS2");
            gameStatsRepository.save(match3);

            // Really good match
            GameStats match4 = new GameStats();
            match4.setUser(user);
            match4.setKills(30);
            match4.setDeaths(11);
            match4.setAssists(9);
            match4.setDurationMinutes(50);
            match4.setCreatedAt(LocalDateTime.now().minusWeeks(1));
            match4.setMap("Nuke");
            match4.setGame("CS2");
            gameStatsRepository.save(match4);
        }

        System.out.println("Created test game statistics for user: " + user.getUsername());
    }
}