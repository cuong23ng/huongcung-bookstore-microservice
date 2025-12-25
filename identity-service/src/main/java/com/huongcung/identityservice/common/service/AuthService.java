package com.huongcung.identityservice.common.service;

import com.huongcung.identityservice.common.dto.AuthResponse;
import com.huongcung.identityservice.common.dto.CustomUserDetails;
import com.huongcung.identityservice.common.dto.LogoutResponse;
import com.huongcung.identityservice.common.dto.RegisterRequest;

public interface AuthService {
    AuthResponse login(CustomUserDetails userDetails);
    AuthResponse register(RegisterRequest request);
    LogoutResponse logout(String authHeader);
}

