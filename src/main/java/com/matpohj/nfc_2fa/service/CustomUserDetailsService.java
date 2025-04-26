package com.matpohj.nfc_2fa.service;

import com.matpohj.nfc_2fa.model.User;
import com.matpohj.nfc_2fa.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

        @Autowired
        private UserRepository userRepository;

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                User user = userRepository.findByUsername(username)
                                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

                return org.springframework.security.core.userdetails.User
                                .withUsername(user.getUsername())
                                .password(user.getPassword())
                                .authorities(user.getRoles().stream()
                                                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                                                .collect(Collectors.toList()))
                                .build();
        }
}