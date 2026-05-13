package com.database.service.impl;

import com.database.entity.User;
import com.database.repo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Loading user by username → {}", username);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found → username: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
        return buildUserDetails(user);
    }

    public UserDetails loadUserByEmail(String email) {
        log.debug("Loading user by email → {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.warn("User not found → email: {}", email);
                    return new UsernameNotFoundException("User not found with email: " + email);
                });
        return buildUserDetails(user);
    }

    public UserDetails loadUserByPhone(String phone) {
        log.debug("Loading user by phone → {}", phone);
        User user = userRepository.findByPhoneNumber(phone)
                .orElseThrow(() -> {
                    log.warn("User not found → phone: {}", phone);
                    return new UsernameNotFoundException("User not found with phone: " + phone);
                });
        return buildUserDetails(user);
    }

    public UserDetails loadUserByEmailAndPhone(String email, String phone) {
        log.debug("Loading user by email + phone → {}, {}", email, phone);
        User user = userRepository.findByEmailAndPhoneNumber(email, phone)
                .orElseThrow(() -> {
                    log.warn("User not found → email: {}, phone: {}", email, phone);
                    return new UsernameNotFoundException("User not found with given email and phone");
                });
        return buildUserDetails(user);
    }

    private UserDetails buildUserDetails(User user) {
        log.debug("Building UserDetails → username: {}", user.getUsername());
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .collect(Collectors.toList())
        );
    }
}