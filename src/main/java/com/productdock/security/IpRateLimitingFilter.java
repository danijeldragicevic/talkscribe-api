package com.productdock.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class IpRateLimitingFilter extends OncePerRequestFilter {

    private static final long REQUESTS_PER_MINUTE = 2L;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain)
            throws ServletException, IOException {

        String ip = request.getRemoteAddr();
        Bucket bucket = buckets.computeIfAbsent(ip, this::newBucket);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("Too many requests from IP: {}", ip);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Too many requests. Please try again later.");
        }
    }

    private Bucket newBucket(String ip) {
        Refill refill = Refill.intervally(REQUESTS_PER_MINUTE, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(REQUESTS_PER_MINUTE, refill);
        return Bucket.builder().addLimit(limit).build();
    }
}
