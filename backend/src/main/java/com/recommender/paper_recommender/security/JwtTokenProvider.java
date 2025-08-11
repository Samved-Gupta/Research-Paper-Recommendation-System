package com.recommender.paper_recommender.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final SecretKey jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final long jwtExpirationInMs = 86400000; // 24 hours

    public String generateToken(Authentication authentication) {
        String subject; // This will be the email or placeholder email

        if (authentication.getPrincipal() instanceof OAuth2User) {
            // --- THIS IS THE NEW, SMARTER LOGIC ---
            OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
            String email = oauth2User.getAttribute("email");

            // If email is null (from GitHub), create our placeholder email from the 'login' attribute.
            if (email == null) {
                String login = oauth2User.getAttribute("login");
                if (login != null) {
                    subject = login + "@github.user.placeholder.com";
                } else {
                    // This is a fallback in case a provider gives neither email nor login.
                    throw new IllegalStateException("Cannot determine user identifier from OAuth2 provider");
                }
            } else {
                // For providers like Google, use the real email.
                subject = email;
            }
        } else {
            // This is for your standard username/password login.
            subject = authentication.getName();
        }

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(subject) // Use the determined subject
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(jwtSecret)
                .compact();
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(token)
                .getBody();
        // This will now correctly return the real email or the placeholder email
        return claims.getSubject();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtSecret).build().parseClaimsJws(authToken);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        }
        return false;
    }
}