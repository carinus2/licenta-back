package com.start.pawpal_finder.auth;

import com.start.pawpal_finder.auth.authorization.AuthorizationTokenFilter;
import com.start.pawpal_finder.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService,
                          PasswordEncoder passwordEncoder) {
        this.customUserDetailsService = customUserDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return customUserDetailsService;
    }

    public static Integer getAuthenticatedUserId() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof String) {
            try {
                return Integer.parseInt((String) principal);
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Principal is not a valid ID: " + principal, e);
            }
        } else {
            throw new IllegalStateException("Principal is not a valid user ID.");
        }
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http,
                                           AuthorizationTokenFilter authorizationTokenFilter) throws Exception {
        http
                // dezactivăm CORS + CSRF (REST stateless JWT)
                .cors().and().csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "api/animals/**").authenticated()
                .requestMatchers(HttpMethod.GET, "api/animals/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "api/animals/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/animals/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "api/animals/**").authenticated()
                .requestMatchers(HttpMethod.GET, "api/pet-owner").authenticated()
                .requestMatchers(HttpMethod.GET, "api/pet-sitter").authenticated()
                .requestMatchers(HttpMethod.POST, "api/posts/**").authenticated()
                .requestMatchers(HttpMethod.GET, "api/posts/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/posts/**").authenticated()
                .requestMatchers(HttpMethod.POST, "api/post-sitter/**").authenticated()
                .requestMatchers(HttpMethod.GET, "api/post-sitter/**").authenticated()
                .requestMatchers(HttpMethod.GET, "api/post-sitter/search").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/post-sitter/**").authenticated()
                .requestMatchers(HttpMethod.PATCH, "/api/post-sitter/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "api/post-sitter/**").authenticated()
                .requestMatchers(HttpMethod.POST, "api/profile/**").authenticated()
                .requestMatchers(HttpMethod.GET, "api/profile/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/profile/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "api/profile/**").authenticated()
                .requestMatchers(HttpMethod.POST, "api/pet-sitter-profile/**").authenticated()
                .requestMatchers(HttpMethod.GET, "api/pet-sitter-profile/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/pet-sitter-profile/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "api/pet-sitter-profile/**").authenticated()
                .requestMatchers(HttpMethod.POST, "api/reservations/**").authenticated()
                .requestMatchers(HttpMethod.GET, "api/reservations/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/reservations/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "api/reservations/**").authenticated()
                .requestMatchers(HttpMethod.POST, "api/notifications/**").authenticated()
                .requestMatchers(HttpMethod.GET, "api/notifications/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/notifications/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "api/notifications/**").authenticated()
                .requestMatchers(HttpMethod.POST, "api/interest-reservations/**").authenticated()
                .requestMatchers(HttpMethod.GET, "api/interest-reservations/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "api/interest-reservations/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "api/interest-reservations/**").authenticated()
                .requestMatchers("/ws-notifications/**").permitAll()
                .requestMatchers("/api/sitter/verification/**").permitAll()
                .anyRequest().authenticated()
                .and()
                // la erori de autentificare, returnăm 401 UNAUTHORIZED (stateless)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                // spunem că nu vrem sesiuni server-side (STATELESS)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // Adăugăm filtru nostru JWT înainte de UsernamePasswordAuthenticationFilter
        http.addFilterBefore(authorizationTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Nu mai este nevoie de Bean separat pentru AuthorizationTokenFilter aici,
    // deoarece îl definim direct prin component scan (vezi pasul următor).
}
