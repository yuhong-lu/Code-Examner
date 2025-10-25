/* service/CacheService.java
package com.CodeExamner.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class CacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    // 题目缓存
    public void cacheProblem(Long problemId, Object problem) {
        String key = "problem:" + problemId;
        set(key, problem, 1, TimeUnit.HOURS);
    }

    public Object getCachedProblem(Long problemId) {
        String key = "problem:" + problemId;
        return get(key);
    }

    // 用户提交统计缓存
    public void cacheUserStats(Long userId, Object stats) {
        String key = "user_stats:" + userId;
        set(key, stats, 30, TimeUnit.MINUTES);
    }

    public Object getCachedUserStats(Long userId) {
        String key = "user_stats:" + userId;
        return get(key);
    }
}

 */