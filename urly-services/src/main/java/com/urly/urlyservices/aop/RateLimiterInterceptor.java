package com.urly.urlyservices.aop;

import com.urly.urlyservices.annotation.RateLimit;
import com.urly.urlyservices.enums.LimitMethod;
import com.urly.urlyservices.enums.LimitType;
import com.urly.urlyservices.ratelimit.RateLimitProcessor;
import com.urly.urlyservices.ratelimit.RateLimiter;
import com.urly.urlyservices.util.ratelimiter.IPUtils;
import com.urly.urlyservices.vo.ratelimiter.RateLimiterInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Slf4j
@Aspect
@Configuration
public class RateLimiterInterceptor {

    private final RateLimitProcessor rateLimitProcessor;

    @Autowired
    public RateLimiterInterceptor(RateLimitProcessor rateLimitProcessor) {
        this.rateLimitProcessor = rateLimitProcessor;
    }

    @Around("execution(public * *(..)) && @annotation(com.urly.urlyservices.annotation.RateLimit)")
    public Object interceptor(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        if (method == null) {
            return null;
        }

        RateLimit rateLimitAnnotation = method.getAnnotation(RateLimit.class);
        LimitType limitType = rateLimitAnnotation.limitType();
        String key = getLimiterKey(request, method, rateLimitAnnotation, limitType);
        key = StringUtils.join(rateLimitAnnotation.prefix(), key);

        double permitsPerSecond = rateLimitAnnotation.permitsPerSecond();
        int period = rateLimitAnnotation.period();
        int permits = rateLimitAnnotation.permits();
        LimitMethod limitMethod = rateLimitAnnotation.limitMethod();

        RateLimiterInfo rateLimiterInfo = new RateLimiterInfo();
        rateLimiterInfo.setKey(key);
        rateLimiterInfo.setPermits(permits);
        rateLimiterInfo.setPeriod(period);
        rateLimiterInfo.setPermitsPerSecond(permitsPerSecond);

        RateLimiter rateLimiter = switch (limitMethod) {
            case FIXED_WINDOW -> rateLimitProcessor.getFixWindowRateLimiter();
            case BUCKET_TOKEN -> rateLimitProcessor.getBucketTokenRateLimiter();
        };

        if (rateLimiter.isRateLimited(key, rateLimiterInfo)) {
            log.info("Access to {} from {} is rate limited", method.getName(), key);
            sendFallback();
            return null;
        }

        return joinPoint.proceed();
    }

    private String getLimiterKey(
            HttpServletRequest request,
            Method method,
            RateLimit rateLimitAnnotation,
            LimitType limitType) {

        String key = StringUtils.upperCase(
                method.getDeclaringClass().getSimpleName() + ":" + method.getName()
        );

        return switch (limitType) {
            case IP -> key + "_" + LimitType.IP + ":" + IPUtils.getClientIpAddress(request);

            case CUSTOMER -> key + "_" + LimitType.CUSTOMER + ":" + rateLimitAnnotation.key();
        };
    }

    private void sendFallback() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = requestAttributes.getResponse();

        response.setContentType("text/html;charset=UTF-8");
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());

        log.info("TOO MANY REQUESTS");
    }


}
