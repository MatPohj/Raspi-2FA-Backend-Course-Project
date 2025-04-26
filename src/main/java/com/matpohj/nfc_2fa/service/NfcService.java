package com.matpohj.nfc_2fa.service;

import com.matpohj.nfc_2fa.model.NfcTag;
import com.matpohj.nfc_2fa.model.User;
import com.matpohj.nfc_2fa.repository.NfcTagRepository;
import com.matpohj.nfc_2fa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
public class NfcService {

    @Autowired
    private NfcTagRepository nfcTagRepository;

    @Autowired
    private UserRepository userRepository;

    public NfcTag registerNfcTag(String tagId, String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + username);
        }

        if (nfcTagRepository.existsByTagId(tagId)) {
            throw new IllegalArgumentException("NFC tag already registered: " + tagId);
        }

        User user = userOpt.get();
        NfcTag nfcTag = new NfcTag(tagId, user);
        return nfcTagRepository.save(nfcTag);
    }

    public boolean validateNfcTag(String tagId, String username) {
        Optional<NfcTag> tagOpt = nfcTagRepository.findByTagId(tagId);
        if (tagOpt.isEmpty() || !tagOpt.get().isActive()) {
            return false;
        }

        NfcTag tag = tagOpt.get();
        User user = tag.getUser();

        return user != null && user.getUsername().equals(username);
    }

    public Set<NfcTag> getUserTags(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + username);
        }

        return nfcTagRepository.findByUser(userOpt.get());
    }

    public void deactivateTag(String tagId) {
        Optional<NfcTag> tagOpt = nfcTagRepository.findByTagId(tagId);
        if (tagOpt.isPresent()) {
            NfcTag tag = tagOpt.get();
            tag.setActive(false);
            nfcTagRepository.save(tag);
        }
    }

    public void setUserNfcRequired(String username, boolean required) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setNfcRequired(required);
            userRepository.save(user);
        }
    }
}