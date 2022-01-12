package com.urly.urlyservices.ratelimit;

import com.urly.urlyservices.vo.RateLimiterInfo;

public interface RateLimiter {

    boolean isRateLimited(String key, RateLimiterInfo rateLimiterInfo);
}
