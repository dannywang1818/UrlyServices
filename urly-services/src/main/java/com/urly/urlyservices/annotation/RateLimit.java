package com.urly.urlyservices.annotation;

import com.urly.urlyservices.enums.LimitMethod;
import com.urly.urlyservices.enums.LimitType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@Inherited

public @interface RateLimit {

    String key() default "RateLimit";

    String prefix() default "Annotation";

    LimitMethod limitMethod() default LimitMethod.FIXED_WINDOW;

    double permitsPerSecond() default 1.0;

    int period() default 1;

    int permits() default 1;

    LimitType limitType() default LimitType.CUSTOMER;
}
