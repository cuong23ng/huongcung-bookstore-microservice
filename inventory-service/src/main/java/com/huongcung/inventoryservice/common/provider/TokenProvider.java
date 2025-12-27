package com.huongcung.inventoryservice.common.provider;

import java.util.Date;

public interface TokenProvider {
    String getUsernameFromToken(String token);
    boolean validateToken(String token);
    Date extractExpiration(String token);
    boolean isTokenExpired(String token);
}
