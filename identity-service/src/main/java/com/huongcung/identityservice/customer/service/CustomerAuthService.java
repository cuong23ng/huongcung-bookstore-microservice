package com.huongcung.identityservice.customer.service;

import com.huongcung.identityservice.common.dto.AuthResponse;
import com.huongcung.identityservice.common.dto.CustomUserDetails;
import com.huongcung.identityservice.common.dto.LogoutResponse;
import com.huongcung.identityservice.common.dto.RegisterRequest;
import com.huongcung.identityservice.common.provider.TokenProvider;
import com.huongcung.identityservice.common.service.AuthService;
import com.huongcung.identityservice.common.service.TokenBlackListService;
import com.huongcung.identityservice.customer.entity.CustomerEntity;
import com.huongcung.identityservice.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("customerAuthService")
@RequiredArgsConstructor
@Slf4j
public class CustomerAuthService implements AuthService {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final TokenBlackListService tokenBlackListService;

    public AuthResponse login(CustomUserDetails userDetails) {

        String token = tokenProvider.generateToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        CustomerEntity customer = customerRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return AuthResponse.builder()
                .token(token)
                .id(customer.getId())
                .email(customer.getEmail())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .roles(roles)
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        log.info("Creating customer account for username: {}", request.getUsername());

        if (customerRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered");
        }

        if (customerRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already registered");
        }

        CustomerEntity customer = new CustomerEntity();
        customer.setUid(UUID.randomUUID().toString());
        customer.setUsername(request.getUsername());
        customer.setEmail(request.getEmail());
        customer.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setPhone(request.getPhone());
        customer.setGender(request.getGender());
        customer.setEmailVerified(false); // TODO: Implement email verification

        CustomerEntity savedCustomer = customerRepository.save(customer);

        CustomUserDetails customUserDetails = CustomUserDetails.create(savedCustomer);

        String token = tokenProvider.generateToken(customUserDetails);

        return AuthResponse.builder()
                .token(token)
                .id(customUserDetails.getId())
                .email(savedCustomer.getEmail())
                .firstName(savedCustomer.getFirstName())
                .lastName(savedCustomer.getLastName())
                .build();
    }

    public LogoutResponse logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return LogoutResponse.failure("Invalid authorization header");
        }

        String token = authHeader.substring(7); // Remove "Bearer " prefix

        if (!tokenProvider.validateToken(token)) {
            return LogoutResponse.failure("Invalid token provided");
        }

        tokenBlackListService.blacklistToken(token);

        log.info("Customer logged out successfully");
        return LogoutResponse.success();
    }

}
