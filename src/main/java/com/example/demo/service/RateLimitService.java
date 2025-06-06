package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
public class RateLimitService {

    private final StringRedisTemplate redisTemplate;
    private final int interval = 10;
    private final int bucketCount = 6;
    private final int limit = 100;

    public RateLimitService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public boolean isAllowed(String userId){

        long now = System.currentTimeMillis() / 1000;
        long currentBucket = now / interval * interval;
        String currentKey = "rate:" + userId + ":" + currentBucket;

        List<String> keys = new ArrayList<>();
        for (int i = 0 ; i < bucketCount ; i++){
            long bucketTime = currentBucket - i * interval;
            keys.add("rate:" + userId + ":" + bucketTime);
        }

        List<String> counts = redisTemplate.opsForValue().multiGet(keys);
        int total = counts.stream()
                .filter(Objects::nonNull)
                .mapToInt(Integer::parseInt)
                .sum();


        if (total >= limit) {
            return false;
        }

        redisTemplate.opsForValue().increment(currentKey, 1);
        redisTemplate.expire(currentKey, 60, TimeUnit.SECONDS);
        return true;
    }
}
