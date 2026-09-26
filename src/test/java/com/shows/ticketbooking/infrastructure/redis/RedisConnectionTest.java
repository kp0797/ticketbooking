package com.shows.ticketbooking.infrastructure.redis;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RedisConnectionTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Test
    void shouldReadAndWriteToRedis() {

        String key = "test:key";
        String value = "hello-redis";

        redisTemplate.opsForValue().set(key, value);

        String result = redisTemplate.opsForValue().get(key);

        assertThat(result).isEqualTo(value);
    }
}