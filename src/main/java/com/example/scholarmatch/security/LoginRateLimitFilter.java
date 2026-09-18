package com.example.scholarmatch.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter {

    @Value("${auth.rate-limit.max-attempts:5}")
    private int maxAttempts;

    @Value("${auth.rate-limit.window-ms:60000}")
    private long windowMs;

    private final ConcurrentHashMap<String, Window> attempts = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();

        if (isLoginEndpoint(path) && "POST".equalsIgnoreCase(request.getMethod())) {
            String key = clientKey(request);
            long now = System.currentTimeMillis();

            Window window = attempts.compute(key, (k, existing) -> {
                if (existing == null || now - existing.windowStart > windowMs) {
                    return new Window(now);
                }
                return existing;
            });

            int count = window.count.incrementAndGet();

            if (count > maxAttempts) {
                response.setStatus(429);
                response.setContentType("application/json");
                response.getWriter().write(
                        "{\"success\":false,\"message\":\"Too many login attempts. Please try again later.\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isLoginEndpoint(String path) {
        return path != null && path.startsWith("/api/auth/") && path.endsWith("/login");
    }

    private String clientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        String ip = (forwardedFor != null && !forwardedFor.isBlank())
                ? forwardedFor.split(",")[0].trim()
                : request.getRemoteAddr();
        return ip + "|" + request.getRequestURI();
    }

    private static class Window {
        final long windowStart;
        final AtomicInteger count = new AtomicInteger(0);

        Window(long windowStart) {
            this.windowStart = windowStart;
        }
    }
}