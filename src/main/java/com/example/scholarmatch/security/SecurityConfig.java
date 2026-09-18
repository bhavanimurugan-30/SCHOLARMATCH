package com.example.scholarmatch.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final LoginRateLimitFilter loginRateLimitFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          LoginRateLimitFilter loginRateLimitFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.loginRateLimitFilter = loginRateLimitFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/institutions/*/documents").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/institutions/*/documents/upload").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/students").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/institutions").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/scholarships/most-viewed").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/scholarships/views/recent").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/scholarships/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/certificate-types/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/search-log/most-searched").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/search-log/recent").hasRole("ADMIN")
                        .requestMatchers("/api/admin-activity-log/**").hasRole("ADMIN")
                        .requestMatchers("/api/admins/**").hasRole("ADMIN")
                        .requestMatchers("/api/institutions/**").hasAnyRole("ADMIN", "INSTITUTION")
                        .requestMatchers("/api/students/**").hasAnyRole("ADMIN", "STUDENT")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(loginRateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter, LoginRateLimitFilter.class);

        return http.build();
    }
}