package com.urly.urlyservices.util.security;

import com.urly.urlyservices.security.userdetail.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import io.jsonwebtoken.*;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
public class JWTUtils {

    public static String generateJWTToken(Authentication authentication, int expiration, SecretKey secretKey) {

        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getId().toString()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + expiration))
                .signWith(secretKey)
                .compact();
    }

    public static Long getIdFromJWTToken(String token, SecretKey secretKey) {
        return Long.parseLong(
                Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody().getSubject()
        );
    }

    public static boolean validateJWTToken(String token, SecretKey secretKey) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (InvalidClaimException e) {
            log.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        }

        return false;
    }
}
