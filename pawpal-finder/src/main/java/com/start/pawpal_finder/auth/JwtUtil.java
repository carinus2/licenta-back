package com.start.pawpal_finder.auth;

import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.entity.PetSitterEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
@Component
public class JwtUtil {

    @Value("${secret.key}")
    private final String SECRET_KEY = "ThisIsTheLongestKeyEverOnThePlanetISwear!";

    public String generateJwtToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 12 * 60 * 60 * 1000)) // 12 ore
                .signWith(getJwtKey())
                .compact();
    }


    private boolean isAdmin(Authentication authentication) {
        Object principal = authentication.getPrincipal();
        if (principal instanceof PetSitterEntity) {
            return ((PetSitterEntity) principal).getAdmin();
        } else if (principal instanceof PetOwnerEntity) {
            return ((PetOwnerEntity) principal).getAdmin();
        }
        return false;
    }


    public boolean validateJwtToken(final String jwt) {
        try {
            return getBuilder().isSigned(jwt);
        } catch (Exception ex) {
            System.out.println("JWT Exception");
        }
        return false;
    }

    private JwtParser getBuilder() {
        return Jwts.parser().setSigningKey(getJwtKey()).build();
    }

    private Key getJwtKey() {
        byte[] keyBytes = SECRET_KEY.getBytes();
        return new SecretKeySpec(keyBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public Claims getClaims(String jwt){
        return getBuilder().parseClaimsJws(jwt).getBody();
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        var headerAuth = request.getHeader("Authorization");

        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
