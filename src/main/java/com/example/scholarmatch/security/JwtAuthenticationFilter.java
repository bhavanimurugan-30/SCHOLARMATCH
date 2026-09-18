package com.example.scholarmatch.security;

import com.example.scholarmatch.institution.repository.InstitutionRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final InstitutionRepository institutionRepository;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, InstitutionRepository institutionRepository) {
        this.jwtUtil = jwtUtil;
        this.institutionRepository = institutionRepository;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtUtil.isTokenValid(token)) {
                Claims claims = jwtUtil.extractClaims(token);
                String email = claims.getSubject();
                String role = claims.get("role", String.class);
                Long id = claims.get("id", Long.class);

                boolean allowed = true;
                if ("INSTITUTION".equals(role)) {
                    allowed = institutionRepository.findById(id)
                            .map(inst -> "APPROVED".equalsIgnoreCase(inst.getVerificationStatus()))
                            .orElse(false);
                }

                if (allowed) {
                    CustomUserPrincipal principal = new CustomUserPrincipal(id, email, role);
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}