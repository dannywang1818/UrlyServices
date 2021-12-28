package com.urly.urlyservices.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private final RedisTemplate<String, Long> redisTemplate;

    @Autowired
    public RedisService(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void set(String key, Long value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object get(String key) {
        if(key == null) {
            return null;
        }
        if(redisTemplate.hasKey(key)) {
            return redisTemplate.opsForValue().get(key);
        }
        return null;
    }
}
