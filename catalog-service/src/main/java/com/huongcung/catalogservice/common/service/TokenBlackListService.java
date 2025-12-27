package com.huongcung.catalogservice.common.service;

public interface TokenBlackListService {
    boolean isTokenBlacklisted(String token);
    void removeFromBlacklist(String token);
    int getBlacklistSize();
    void clearBlacklist();
}
