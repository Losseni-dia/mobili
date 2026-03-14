package com.mobili.backend.infrastructure.security;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import com.mobili.backend.infrastructure.security.token.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
                        // 1. ACCÈS PUBLIC
                        .requestMatchers("/uploads/**").permitAll()
                        .requestMatchers("/v1/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/trips", "/v1/trips/**").permitAll()
                        .requestMatchers("/v1/payments/callback").permitAll()

                        // 2. INSCRIPTION PARTENAIRE (Utilisateur déjà connecté)
                        .requestMatchers(HttpMethod.POST, "/v1/partners").authenticated()

                        // 3. GESTION DES TRAJETS (POST, PUT, DELETE)
                        // Note : hasAnyAuthority est plus fiable car il matche exactement
                        // "ROLE_PARTNER"
                        .requestMatchers(HttpMethod.POST, "/v1/trips", "/v1/trips/**")
                        .hasAnyAuthority("ROLE_PARTNER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/v1/trips/**").hasAnyAuthority("ROLE_PARTNER", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/v1/trips/**")
                        .hasAnyAuthority("ROLE_PARTNER", "ROLE_ADMIN")

                        // 4. PROFIL ET RÉSERVATIONS
                        .requestMatchers("/v1/auth/me").hasAnyAuthority("ROLE_USER", "ROLE_PARTNER", "ROLE_ADMIN")
                        .requestMatchers("/v1/bookings/**").hasAnyAuthority("ROLE_USER", "ROLE_PARTNER","ROLE_ADMIN")
                        .requestMatchers("/v1/tickets/**")
                        .hasAnyAuthority("ROLE_USER", "ROLE_PARTNER", "ROLE_CHAUFFEUR", "ROLE_ADMIN")

                        // 5. ADMINISTRATION DES PARTENAIRES
                        .requestMatchers(HttpMethod.PUT, "/v1/partners/**")
                        .hasAnyAuthority("ROLE_PARTNER", "ROLE_ADMIN")
                        .requestMatchers("/v1/partners/**").hasAnyAuthority("ROLE_ADMIN")
                        .requestMatchers("/v1/partners/my-company").hasAnyRole("PARTNER", "ADMIN")
                        .requestMatchers("/v1/admin/**").hasAnyAuthority("ROLE_ADMIN")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter,
                        org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "X-Requested-With"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}