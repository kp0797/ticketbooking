package com.shows.ticketbooking.infrastructure.redis;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import com.shows.ticketbooking.show.dto.ShowSummary;
import tools.jackson.core.type.TypeReference;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

@Component
public class ShowCache {
    
    // private static final Duration CACHE_TTL = Duration.ofMinutes(5);
    private final RedisTemplate<String, String> redisTemplate;
    private final JsonMapper jsonMapper;
    private final Duration cacheTtl;

    public ShowCache(RedisTemplate<String, String> redisTemplate, JsonMapper jsonMapper,@Value("${app.cache.shows.ttl:5m}") Duration cacheTtl) {
        this.redisTemplate = redisTemplate;
        this.jsonMapper  = jsonMapper;
        this.cacheTtl = cacheTtl;
    }
    // get shows from cache
    public Optional<List<ShowSummary>> getShows(Long movieId) {

        String key = "shows:movie:" + movieId;
        String json = redisTemplate.opsForValue().get(key);
        if (json == null) {
            return Optional.empty();
        }
        try {
            List<ShowSummary> shows = jsonMapper.readValue(json, new TypeReference<List<ShowSummary>>() {});
            return Optional.of(shows);
        } catch(JacksonException e) {
            throw new RuntimeException(
                    "Failed to deserialize cached shows", e
            );
        }
    }

    // put shows in cache
    public void putShows(Long movieId, List<ShowSummary> shows) {
        String key = "shows:movie:" + movieId;
        try {
            String json = jsonMapper.writeValueAsString(shows);
            redisTemplate.opsForValue().set(key, json, cacheTtl);
        } catch(JacksonException e) {
            throw new RuntimeException(
                "Failed to serialize shows", e
            );
        }
    }

    // evict show from cache
    public void evictShows(Long movieId) {
        String key = "shows:movie:" + movieId;
        try {
            redisTemplate.delete(key);
        } catch(Exception e) {
            throw new RuntimeException(
                "Failed to delete cache entry", e);
        }
    }
}
