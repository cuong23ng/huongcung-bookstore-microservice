package com.huongcung.storefrontgateway.filter;

import com.huongcung.storefrontgateway.configuration.RouterValidator;
import com.huongcung.storefrontgateway.provider.TokenProvider;
import com.huongcung.storefrontgateway.service.TokenBlackListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
@Slf4j
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    private final RouterValidator routerValidator;
    private final TokenProvider tokenProvider;
    private final TokenBlackListService tokenBlackListService;

    public AuthenticationFilter(RouterValidator routerValidator, TokenProvider tokenProvider, TokenBlackListService tokenBlackListService) {
        super(Config.class);
        this.routerValidator = routerValidator;
        this.tokenProvider = tokenProvider;
        this.tokenBlackListService = tokenBlackListService;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {

            if (routerValidator.isSecured.test(exchange.getRequest())) {

                if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not found token");
                }

                String token = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                if (token != null && token.startsWith("Bearer ")) {
                    token = token.substring(7);
                }

                Boolean isBlacklisted = tokenBlackListService.isTokenBlacklisted(token);
                if (Boolean.TRUE.equals(isBlacklisted)) {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Token");
                }

                try {
                    boolean isValidToken = tokenProvider.validateToken(token);
                    if (isValidToken) {
                        log.info("Valid token, {}", token);
                    } else {
                        log.info("Invalid token, {}", token);
                        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token");
                    }

                } catch (Exception e) {
                    log.info("Invalid token, {}", e.getMessage());
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid token");
                }
            }

            return chain.filter(exchange);
        });
    }

    public static class Config {
    }
}