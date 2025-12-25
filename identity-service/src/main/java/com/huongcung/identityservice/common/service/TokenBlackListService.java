package com.huongcung.identityservice.common.service;

public interface TokenBlackListService {
    void blacklistToken(String token);
    boolean isTokenBlacklisted(String token);
    void removeFromBlacklist(String token);
    int getBlacklistSize();
    void clearBlacklist();
}
