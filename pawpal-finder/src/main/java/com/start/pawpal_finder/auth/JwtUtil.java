package com.start.pawpal_finder.auth;

import com.start.pawpal_finder.entity.PetOwnerEntity;
import com.start.pawpal_finder.entity.PetSitterEntity;
import com.start.pawpal_finder.service.PetOwnerService;
import com.start.pawpal_finder.service.PetSitterService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private final PetSitterService petSitterService;

    private final PetOwnerService petOwnerService;

    @Autowired
    public JwtUtil(PetSitterService petSitterService, PetOwnerService petOwnerService) {
        this.petSitterService = petSitterService;
        this.petOwnerService = petOwnerService;
    }

    @Value("${secret.key}")
    private final String SECRET_KEY = "ThisIsTheLongestKeyEverOnThePlanetISwear!";

    public String generateJwtToken(final Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        String email = userDetails.getUsername();
        Integer userId = null;
        Optional<PetOwnerEntity> petOwner = petOwnerService.findByEmail(email);
        if (petOwner.isPresent()) {
            userId = petOwner.get().getId();
        } else {
            Optional<PetSitterEntity> petSitter = petSitterService.findByEmail(email);
            if (petSitter.isPresent()) {
                userId = petSitter.get().getId();
            }
        }

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("roles", roles)
                .claim("email", email)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 12 * 60 * 60 * 1000))
                .signWith(getJwtKey())
                .compact();
    }

    public String generateAdminJwtToken(final Authentication authentication) {

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("roles", List.of("ROLE_ADMIN"))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 12 * 60 * 60 * 1000))
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
