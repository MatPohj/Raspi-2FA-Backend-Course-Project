package com.matpohj.nfc_2fa.repository;

import com.matpohj.nfc_2fa.model.NfcTag;
import com.matpohj.nfc_2fa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface NfcTagRepository extends JpaRepository<NfcTag, Long> {
    Optional<NfcTag> findByTagId(String tagId);
    Set<NfcTag> findByUser(User user);
    boolean existsByTagId(String tagId);
}