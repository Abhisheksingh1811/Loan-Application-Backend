package com.yasirakbal.secureloanapi.common.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;

import java.time.Duration;

@Service
public class RedisService {

    private final JedisPooled jedis;

    public RedisService(
            @Value("${app.redis.host:localhost}") String redisHost,
            @Value("${app.redis.port:6379}") int redisPort
    ) {
        this.jedis = new JedisPooled(redisHost, redisPort);
    }

    public void set(String key, String value, Duration ttl) {
        jedis.setex(key, ttl.toSeconds(), value);
    }

    public boolean exists(String key) {
        return jedis.exists(key);
    }

    public void delete(String key) {
        jedis.del(key);
    }

    public long increment(String key) {
        return jedis.incr(key);
    }

    public void expire(String key, Duration ttl) {
        jedis.expire(key, ttl.toSeconds());
    }

    public long getTtlSeconds(String key) {
        return jedis.ttl(key);
    }
}