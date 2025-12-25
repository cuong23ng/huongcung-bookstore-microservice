package com.huongcung.identityservice.common.filter;

import com.huongcung.identityservice.common.configuration.JwtConfiguration;
import com.huongcung.identityservice.common.enumeration.UserType;
import com.huongcung.identityservice.common.provider.TokenProvider;
import com.huongcung.identityservice.common.service.TokenBlackListService;
import com.huongcung.identityservice.customer.service.CustomerUserDetailsService;
import com.huongcung.identityservice.staff.service.StaffUserDetailsService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.server.ResponseStatusException;

import javax.crypto.SecretKey;
import java.io.IOException;

@Primary
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    @Qualifier("customerUserDetailsService")
    private final CustomerUserDetailsService customerUserDetailsService;
    @Qualifier("staffUserDetailsService")
    private final StaffUserDetailsService staffUserDetailsService;
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

                UserDetails userDetails = null;
                if (userTypeEnum == UserType.STAFF) {
                    userDetails = staffUserDetailsService.loadUserByUsername(username);
                }

                if (userTypeEnum == UserType.CUSTOMER) {
                    userDetails = customerUserDetailsService.loadUserByUsername(username);
                }

                if (userDetails != null) {
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()); //TODO: credentials
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authentication);
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
