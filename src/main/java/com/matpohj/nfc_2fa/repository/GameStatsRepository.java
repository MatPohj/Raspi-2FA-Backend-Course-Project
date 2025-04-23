package com.matpohj.nfc_2fa.repository;

import com.matpohj.nfc_2fa.model.GameStats;
import com.matpohj.nfc_2fa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameStatsRepository extends JpaRepository<GameStats, Long> {
    List<GameStats> findByUserOrderByCreatedAtDesc(User user);
}