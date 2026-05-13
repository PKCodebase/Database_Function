package com.database.service.impl;




import com.database.dto.LoginRequest;
import com.database.dto.RefreshTokenRequest;
import com.database.dto.RegisterRequest;
import com.database.entity.User;
import com.database.enums.Role;
import com.database.repo.UserRepository;
import com.database.response.AuthResponse;
import com.database.util.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtils jwtUtils,
                       CustomUserDetailsService userDetailsService,
                       RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
        this.refreshTokenService = refreshTokenService;
    }

    public String register(RegisterRequest request) {
        log.info("Registering user → email: {}", request.getEmail());
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        Role role;
        try {
            role = Role.valueOf(request.getRole() != null
                    ? request.getRole().toUpperCase() : "ROLE_USER");
        } catch (IllegalArgumentException e) {
            log.warn("Invalid role provided, defaulting to ROLE_USER");
            role = Role.ROLE_USER;
        }

        user.setRoles(Set.of(role));
        userRepository.save(user);
        log.info("User registered successfully → email: {}, role: {}", request.getEmail(), role.name());
        return "User registered successfully with role: " + role.name();
    }

    public AuthResponse login(LoginRequest request) {
        log.info("Login attempt → email: {}, phone: {}", request.getEmail(), request.getPhoneNumber());
        UserDetails userDetails;
        boolean hasEmail = request.getEmail() != null && !request.getEmail().isBlank();
        boolean hasPhone = request.getPhoneNumber() != null && !request.getPhoneNumber().isBlank();

        if (hasEmail && hasPhone) {
            log.debug("Login by email + phone");
            userDetails = userDetailsService.loadUserByEmailAndPhone(request.getEmail(), request.getPhoneNumber());
        } else if (hasEmail) {
            log.debug("Login by email");
            userDetails = userDetailsService.loadUserByEmail(request.getEmail());
        } else if (hasPhone) {
            log.debug("Login by phone");
            userDetails = userDetailsService.loadUserByPhone(request.getPhoneNumber());
        } else {
            log.warn("Login failed → no email or phone provided");
            throw new BadCredentialsException("Provide email or phone number to login");
        }

        if (!passwordEncoder.matches(request.getPassword(), userDetails.getPassword())) {
            log.warn("Login failed → invalid password for user: {}", userDetails.getUsername());
            throw new BadCredentialsException("Invalid password");
        }

        String accessToken = jwtUtils.generateAccessToken(userDetails.getUsername());
        String refreshToken = refreshTokenService.createRefreshToken(userDetails.getUsername());
        log.info("Login successful → username: {}", userDetails.getUsername());
        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refreshAccessToken(RefreshTokenRequest request) {
        log.info("Refreshing access token");
        String username = refreshTokenService.validateAndGetUsername(request.getRefreshToken());
        String newAccessToken = jwtUtils.generateAccessToken(username);
        log.info("Access token refreshed → username: {}", username);
        return new AuthResponse(newAccessToken, request.getRefreshToken());
    }

    public void logout(String refreshToken) {
        log.info("Logout → deleting refresh token");
        refreshTokenService.deleteRefreshToken(refreshToken);
        log.info("Logout successful");
    }
}
