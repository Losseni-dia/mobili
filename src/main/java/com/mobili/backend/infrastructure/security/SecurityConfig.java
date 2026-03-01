package com.mobili.backend.infrastructure.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.mobili.backend.infrastructure.security.token.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/trips/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/v1/bookings/**").hasAnyAuthority("USER", "PARTNER")
                        .requestMatchers("/v1/users/me").hasAnyRole("USER", "ADMIN","PARTNER")
                        .requestMatchers("/v1/trips/**").hasAnyRole("ADMIN", "PARTNER")
                        // 1. D'abord, on autorise l'inscription (POST) pour tous les connectés
                        .requestMatchers(HttpMethod.POST, "/v1/partners").permitAll()

                        // 2. Ensuite, on autorise les partenaires à modifier leur propre profil
                        .requestMatchers(HttpMethod.PUT, "/v1/partners/**").hasRole("PARTNER")

                        // 3. Enfin, le reste de /v1/partners est pour l'ADMIN (GET all, DELETE, etc.)
                        .requestMatchers("/v1/partners/**").hasRole("ADMIN")
                        .anyRequest().authenticated()) // La parenthèse doit se fermer ici
                .addFilterBefore(jwtAuthFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200")); // Ton front
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // On applique à TOUTES les routes
        return source;
    }

    // À ajouter dans SecurityConfig.java
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // BCrypt est le standard actuel pour sécuriser les mots de passe
        return new BCryptPasswordEncoder();
    }
}