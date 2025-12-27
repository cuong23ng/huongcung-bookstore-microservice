package com.huongcung.inventoryservice.common.filter;

import com.huongcung.inventoryservice.common.configuration.JwtConfiguration;
import com.huongcung.inventoryservice.common.enumeration.UserType;
import com.huongcung.inventoryservice.common.provider.TokenProvider;
import com.huongcung.inventoryservice.common.service.TokenBlackListService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

@Primary
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final JwtConfiguration jwtConfiguration;
    private final TokenBlackListService tokenBlackListService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String token = getJwtFromRequest(request);

            if (StringUtils.hasText(token) && tokenProvider.validateToken(token)) {

                if (tokenBlackListService.isTokenBlacklisted(token)) {
                    log.info("Token is blacklisted, rejecting request");
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Token");
                }
                
                String username = tokenProvider.getUsernameFromToken(token);
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(getSigningKey())
                        .build()
                        .parseClaimsJws(token)
                        .getBody();
                String userType = claims.get("type", String.class);
                UserType userTypeEnum = UserType.valueOf(userType.toUpperCase());

                log.info("Validate token for {}", userType);

                if (userTypeEnum != UserType.STAFF) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only staffs have authority.");
                }

                List<String> roles = claims.get("roles", List.class);

                List<SimpleGrantedAuthority> authorities = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .toList();

                if (username != null && !authorities.isEmpty()) {
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
                }
            }
        } catch (Exception ex) {
            log.info("Could not set user authentication in security context", ex);
            throw ex;
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(jwtConfiguration.getHeaderName());
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(jwtConfiguration.getTokenPrefix())) {
            return bearerToken.substring(jwtConfiguration.getTokenPrefix().length());
        }
        return null;
    }

    private SecretKey getSigningKey() {
        String secret = jwtConfiguration.getSecret();

        // Validate secret length for HS512 (minimum 64 characters = 512 bits)
        if (secret.length() < 64) {
            throw new IllegalArgumentException(
                    "JWT secret must be at least 64 characters long for HS512 algorithm. " +
                            "Current length: " + secret.length() + " characters. " +
                            "Please update your JWT_SECRET environment variable or application.yml"
            );
        }

        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
