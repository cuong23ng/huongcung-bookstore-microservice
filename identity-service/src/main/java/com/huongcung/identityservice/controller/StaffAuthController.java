package com.huongcung.identityservice.controller;

import com.huongcung.identityservice.common.dto.*;
import com.huongcung.identityservice.common.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/admin/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*", maxAge = 3600)
public class StaffAuthController {

    @Qualifier("staffAuthenticationManager")
    private final AuthenticationManager authenticationManager;

    @Qualifier("staffAuthService")
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest authRequest) {
        log.info("Staff login attempt for username: {}", authRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            AuthResponse response = authService.login(userDetails);
            log.info("Staff login successful for user: {}", authRequest.getUsername());
            return ResponseEntity.ok(response);
        } else {
            throw new RuntimeException("Wrong login information!");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> addNewUser(@RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("Authorization") String authHeader) {
        log.info("Staff logout attempt with Authorization header");
        LogoutResponse response = authService.logout(authHeader);
        log.info("Staff logout result: {}, {}", response.isSuccess(), response.getMessage());
        return ResponseEntity.ok(response);
    }



    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of("status", "Staff authentication service is running"));
    }
}

