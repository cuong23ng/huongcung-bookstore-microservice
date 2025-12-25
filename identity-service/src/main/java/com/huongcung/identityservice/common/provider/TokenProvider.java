package com.huongcung.identityservice.common.provider;

import com.huongcung.identityservice.common.dto.CustomUserDetails;

import java.util.Date;

public interface TokenProvider {
    String generateToken(CustomUserDetails userDetails);
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
    Date extractExpiration(String token);
    boolean isTokenExpired(String token);
}
