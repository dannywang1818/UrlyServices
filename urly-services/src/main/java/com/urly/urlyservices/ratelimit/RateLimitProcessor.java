package com.urly.urlyservices.ratelimit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.util.concurrent.RateLimiter;
import com.urly.urlyservices.config.LocalCacheProperties;
import com.urly.urlyservices.vo.RateLimiterInfo;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
@SuppressWarnings("UnstableApiUsage")
public class RateLimitProcessor {

    private final LocalCacheProperties localCacheProperties;
    private LoadingCache<RateLimiterInfo, RateLimiter> rateLimiterCache;
    private ConcurrentHashMap<String, RateLimiter> rateLimiters;

    @Getter
    private final FixWindowRateLimiter fixWindowRateLimiter;

    @Getter
    private final BucketTokenRateLimiter bucketTokenRateLimiter;

    @Autowired
    public RateLimitProcessor(
            LocalCacheProperties localCacheProperties,
            FixWindowRateLimiter fixWindowRateLimiter,
            BucketTokenRateLimiter bucketTokenRateLimiter
    ) {
        this.localCacheProperties = localCacheProperties;
        this.fixWindowRateLimiter = fixWindowRateLimiter;
        this.bucketTokenRateLimiter = bucketTokenRateLimiter;
    }

    @PostConstruct
    private void createRateLimiterCache() {
        rateLimiters = new ConcurrentHashMap<>();
        rateLimiterCache = CacheBuilder
                .newBuilder()
                .expireAfterAccess(localCacheProperties.getKeepAliveTime(), TimeUnit.MINUTES)
                .build(new CacheLoader<RateLimiterInfo, RateLimiter>() {
                    @Override
                    @Nonnull
                    public RateLimiter load(@Nonnull RateLimiterInfo rateLimiterInfo) throws Exception {
                        return RateLimiter.create(rateLimiterInfo.getPermitsPerSecond());
                    }
                });
    }

    public RateLimiter getRateLimiter(RateLimiterInfo key) {
        return rateLimiterCache.getUnchecked(key);
    }

    public RateLimiter getRateLimiter(String key, double permitsPerSecond) {
        RateLimiter rateLimiter = rateLimiters.get(key);
        if (rateLimiter == null) {
            rateLimiter = RateLimiter.create(permitsPerSecond);
            rateLimiters.put(key, rateLimiter);
        }

        return rateLimiter;
    }

    public static boolean isRateLimited(RateLimiter rateLimiter, int period, int permits) {
        boolean tryAcquire = rateLimiter.tryAcquire(permits, period, TimeUnit.SECONDS);
        return !tryAcquire;
    }
}
