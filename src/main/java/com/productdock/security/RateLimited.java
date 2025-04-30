package com.productdock.security;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimited {
    int requests() default 1;
    int durationMinutes() default 1;
}
