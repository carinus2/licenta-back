package com.start.pawpal_finder.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    @Value("${secret.key}")
    private final String SECRET_KEY = "ThisIsTheLongestKeyEverOnThePlanetISwear!";

    public String generateJwtToken(final Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 12 * 60 * 60 * 1000))
                .signWith(getJwtKey())
                .compact();
    }

    public String generateAdminJwtToken(final Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        roles.add("ROLE_ADMIN");

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", roles)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 12 * 60 * 60 * 1000)) // 12 ore
                .signWith(getJwtKey())
                .compact();
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
