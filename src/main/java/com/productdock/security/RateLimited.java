package com.productdock.security;

import java.lang.annotation.*;

/**
 * Annotation to enable rate limiting on a method.
 * <p>
 * Usage: Place this annotation on a controller or service method to restrict
 * the number of allowed requests from a single client within a specified time window.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimited {
    int requests() default 1;
    int durationMinutes() default 1;
}
