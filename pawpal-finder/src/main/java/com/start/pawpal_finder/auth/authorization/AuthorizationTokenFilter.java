package com.start.pawpal_finder.auth.authorization;


import com.start.pawpal_finder.auth.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class AuthorizationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            var jwt = jwtUtils.getJwtFromRequest(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                var jwsClaims = jwtUtils.getClaims(jwt);

                List<String> roles = jwsClaims.get("roles", ArrayList.class);
                var username = jwsClaims.getSubject();

                var entitlements = roles.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(toList());

                var authentication = new UsernamePasswordAuthenticationToken(username, null, entitlements);

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (final Exception e) {
            System.out.println("Cannot set authentication");
        }
        filterChain.doFilter(request, response);
    }
}