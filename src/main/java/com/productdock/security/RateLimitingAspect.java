package com.productdock.security;

import com.productdock.exception.TooManyRequestsException;
import io.github.bucket4j.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.stereotype.Component;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Aspect
@Component
public class RateLimitingAspect {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Around("@annotation(com.productdock.security.RateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String ip = request.getRemoteAddr();

        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        RateLimited annotation = method.getAnnotation(RateLimited.class);

        int requests = annotation.requests();
        Duration duration = Duration.ofMinutes(annotation.durationMinutes());

        Bucket bucket = buckets.computeIfAbsent(ip, key -> newBucket(requests, duration));

        if (!bucket.tryConsume(1)) {
            throw new TooManyRequestsException("Too many requests from IP: " + ip);
        }

        return joinPoint.proceed();
    }

    private Bucket newBucket(int requests, Duration duration) {
        Refill refill = Refill.intervally(requests, duration);
        Bandwidth limit = Bandwidth.classic(requests, refill);
        return Bucket.builder().addLimit(limit).build();
    }
}
