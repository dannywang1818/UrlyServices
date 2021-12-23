package com.urly.urlyservices.ratelimit;

import com.urly.urlyservices.vo.ratelimiter.RateLimiterInfo;

public interface RateLimiter {

    boolean isRateLimited(String key, RateLimiterInfo rateLimiterInfo);
}
