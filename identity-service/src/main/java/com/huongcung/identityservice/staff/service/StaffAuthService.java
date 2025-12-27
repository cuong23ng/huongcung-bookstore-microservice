package com.huongcung.identityservice.staff.service;

import com.huongcung.identityservice.common.dto.CustomUserDetails;
import com.huongcung.identityservice.common.dto.AuthResponse;
import com.huongcung.identityservice.common.dto.LogoutResponse;
import com.huongcung.identityservice.common.dto.RegisterRequest;
import com.huongcung.identityservice.common.provider.TokenProvider;
import com.huongcung.identityservice.common.service.TokenBlackListService;
import com.huongcung.identityservice.staff.entity.StaffEntity;
import com.huongcung.identityservice.staff.enumeration.StaffRole;
import com.huongcung.identityservice.staff.enumeration.StaffType;
import com.huongcung.identityservice.staff.repository.StaffRepository;
import com.huongcung.identityservice.common.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service("staffAuthService")
@RequiredArgsConstructor
@Slf4j
public class StaffAuthService implements AuthService {

    private final StaffRepository staffRepository;
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final TokenBlackListService tokenBlacklistService;

    public AuthResponse login(CustomUserDetails userDetails) {

        String token = tokenProvider.generateToken(userDetails);

        List<String> roles = userDetails.getAuthorities().parallelStream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        StaffEntity staff = staffRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        return AuthResponse.builder()
                .token(token)
                .id(staff.getId())
                .email(staff.getEmail())
                .firstName(staff.getFirstName())
                .lastName(staff.getLastName())
                .roles(roles)
                .build();
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        log.info("Creating staff account for email: {}, staffType: {}", request.getEmail(), request.getStaffType());

        // Validate staffType is not ADMIN
//        if (request.getStaffType() == StaffType.ADMIN) {
//            throw new IllegalArgumentException("ADMIN staff type cannot be created via this endpoint. Admins must be created separately.");
//        }

        // Validate staffType is either STORE_MANAGER or SUPPORT_AGENT
//        if (request.getStaffType() != StaffType.STORE_MANAGER && request.getStaffType() != StaffType.SUPPORT_AGENT) {
//            throw new IllegalArgumentException("Staff type must be either STORE_MANAGER or SUPPORT_AGENT");
//        }

        if (request.getStaffType() == StaffType.STORE_MANAGER ||
                request.getStaffType() == StaffType.WAREHOUSE_MANAGER ||
                request.getStaffType() == StaffType.WAREHOUSE_STAFF) {

            if (request.getCity() == null) {
                throw new IllegalArgumentException("assignedCity is required when staffType is " + request.getStaffType());
            }

        }

        if (staffRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered");
        }

        if (staffRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already registered");
        }

        StaffEntity staff = new StaffEntity();
        staff.setUid(UUID.randomUUID().toString());
        staff.setEmail(request.getEmail());
        staff.setUsername(request.getUsername());
        staff.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        staff.setFirstName(request.getFirstName());
        staff.setLastName(request.getLastName());
        staff.setGender(request.getGender());
        staff.setPhone(request.getPhone());
        staff.setStaffType(request.getStaffType());
        staff.setCity(request.getCity());
        staff.setEmailVerified(false); //TODO: Verify by email

        StaffEntity savedStaff = staffRepository.save(staff);

        log.info("Staff account created successfully with ID: {}, username: {}", savedStaff.getId(), savedStaff.getUsername());

        return AuthResponse.builder()
                .id(staff.getId())
                .username(staff.getUsername())
                .email(staff.getEmail())
                .roles(staff.getStaffType().getRoles().stream().map(StaffRole::name).toList())
                .firstName(staff.getFirstName())
                .lastName(staff.getLastName())
                .build();
    }

    public LogoutResponse logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return LogoutResponse.failure("Invalid authorization header");
        }

        String token = authHeader.substring(7);

        if (!tokenProvider.validateToken(token)) {
            return LogoutResponse.failure("Invalid token provided");
        }

        tokenBlacklistService.blacklistToken(token);

        log.info("Staff logged out successfully");
        return LogoutResponse.success();
    }
}

