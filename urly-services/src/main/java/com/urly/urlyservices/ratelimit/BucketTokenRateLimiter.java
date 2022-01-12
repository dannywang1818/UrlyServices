package com.urly.urlyservices.ratelimit;

import com.urly.urlyservices.vo.RateLimiterInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class BucketTokenRateLimiter implements RateLimiter{

    private final RedisTemplate<String, Serializable> limitRedisTemplate;

    @Autowired
    @Qualifier("limiterScript")
    private RedisScript<Long> redisLimiterScript;

    @Autowired
    public BucketTokenRateLimiter(RedisTemplate<String, Serializable> limitRedisTemplate) {
        this.limitRedisTemplate = limitRedisTemplate;
    }

    @Override
    public boolean isRateLimited(String key, RateLimiterInfo rateLimiterInfo) {
        List<String> keys = getKeys(key);

        double permitsPerSecond = rateLimiterInfo.getPermitsPerSecond();
        int limit = rateLimiterInfo.getPermits();

        Number count = limitRedisTemplate.execute(
                redisLimiterScript,
                keys,
                permitsPerSecond,
                limit,
                getCurrentTimeStamp(),
                1);

        return count.intValue() != 1;
    }

    private static List<String> getKeys(String id) {
        String prefix = "rate_limiter.{" + id;
        String tokenKey = prefix + "}.tokens";
        String timestampKey = prefix + "}.timestamp";
        return Arrays.asList(tokenKey, timestampKey);
    }

    private static long getCurrentTimeStamp() {
        Instant instant = Instant.now();
        return instant.getEpochSecond();
    }
}
