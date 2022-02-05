package com.urly.urlyservices.ratelimit;

import com.urly.urlyservices.vo.RateLimiterInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;

@Slf4j
@Component
public class FixWindowRateLimiter implements RateLimiter{

    private static final int EXPIRATION_FUDGE = 5;
    private final RedisTemplate<String, Serializable> limitRedisTemplate;

    @Autowired
    public FixWindowRateLimiter(RedisTemplate<String, Serializable> limitRedisTemplate) {
        this.limitRedisTemplate = limitRedisTemplate;
    }

    @Override
    public boolean isRateLimited(String key, RateLimiterInfo rateLimiterInfo) {
        int period = rateLimiterInfo.getPeriod();
        int limit = rateLimiterInfo.getPermits();

        long window = getWindow(key, period);
        String cacheKey = makeCacheKey(window, key, limit, period);

        Long count = limitRedisTemplate.opsForValue().increment(cacheKey);
        if (count != null && count == 1) {
            limitRedisTemplate.expire(cacheKey, period + EXPIRATION_FUDGE, TimeUnit.SECONDS);
        }

        if (limitRedisTemplate.getExpire(cacheKey) == -1) {
            limitRedisTemplate.expire(cacheKey, period + EXPIRATION_FUDGE, TimeUnit.SECONDS);
        }

        return count > limit;
    }

    private static long getWindow(String key, int period) {
        long timeStamp = getCurrentTimeStamp();

        if (period == 1) {
            return timeStamp;
        }

        byte[] keyInBytes = StringUtils.isNotBlank(key) ? key.getBytes(StandardCharsets.UTF_8) : null;

        long staggeredWindow = keyInBytes != null ? getStaggeredWindow(keyInBytes, period) : 0L;

        long window = timeStamp - (timeStamp % period) + staggeredWindow;

        return window < timeStamp? window + period: window;
    }

    private static long getCurrentTimeStamp() {
        Instant instant = Instant.now();

        return instant.getEpochSecond();
    }

    private static long getStaggeredWindow(byte[] keyInBytes, int period) {
        CRC32 crc32 = new CRC32();
        crc32.update(keyInBytes);

        return crc32.getValue() % period;
    }

    private static String makeCacheKey(long window, String key, int limit, int period) {
        String keyStr = StringUtils.join(limit, period, key, window);
        return DigestUtils.md5Hex(keyStr);
    }
}
