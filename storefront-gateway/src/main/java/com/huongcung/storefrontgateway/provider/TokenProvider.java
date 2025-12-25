package com.huongcung.storefrontgateway.provider;

import java.util.Date;

public interface TokenProvider {
    boolean validateToken(String token);
    Date extractExpiration(String token);
    boolean isTokenExpired(String token);
}
