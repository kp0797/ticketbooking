package com.shows.ticketbooking.infrastructure.redis;

import com.shows.ticketbooking.show.dto.ShowSummary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = "app.cache.shows.ttl = 2s")
class ShowCacheTest {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ShowCache showCache;

    @Test
    void shouldCacheShowsWithFiveMinuteTtl() {

        Long movieId = System.nanoTime();
        String key = "shows:movie:" + movieId;

        try {
            // Arrange
            ShowSummary summary = new ShowSummary();
            summary.setShowId(101L);
            summary.setMovieName("Interstellar");
            summary.setTheaterName("PVR");
            summary.setScreenName("Screen 1");

            OffsetDateTime startTime = OffsetDateTime.now();
            summary.setStartTime(startTime);

            List<ShowSummary> summaries = List.of(summary);

            // Act: store the summaries in Redis
            showCache.putShows(movieId, summaries);

            // Assert: Redis key exists
            assertThat(redisTemplate.hasKey(key)).isTrue();

            // Assert: remaining TTL is positive and at most 300 seconds
            Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);

            assertThat(ttl).isNotNull();
            assertThat(ttl).isPositive();
            assertThat(ttl).isLessThanOrEqualTo(300L);

            // Act: retrieve the cached summaries
            Optional<List<ShowSummary>> cachedShows =
                    showCache.getShows(movieId);

            // Assert: cached data exists
            assertThat(cachedShows).isPresent();

            List<ShowSummary> actualShows = cachedShows.orElseThrow();

            assertThat(actualShows).hasSize(1);

            ShowSummary actual = actualShows.get(0);

            // Assert: DTO fields match
            assertThat(actual.getShowId())
                    .isEqualTo(summary.getShowId());

            assertThat(actual.getMovieName())
                    .isEqualTo(summary.getMovieName());

            assertThat(actual.getTheaterName())
                    .isEqualTo(summary.getTheaterName());

            assertThat(actual.getScreenName())
                    .isEqualTo(summary.getScreenName());

            // Compare the same instant, regardless of UTC offset
            assertThat(actual.getStartTime().toInstant())
                    .isEqualTo(startTime.toInstant());

        } finally {
            // Cleanup: remove test data from Redis
            redisTemplate.delete(key);
        }
    }

    @Test
    void shouldExpireCachedShowsAfterTtl() throws InterruptedException {

        Long movieId = System.nanoTime();
        String key = "shows:movie:" + movieId;

        try {
            ShowSummary summary = new ShowSummary();
            summary.setShowId(101L);
            summary.setMovieName("Interstellar");
            summary.setTheaterName("PVR");
            summary.setScreenName("Screen 1");
            summary.setStartTime(OffsetDateTime.now());

            // Store the summary in Redis
            showCache.putShows(movieId, List.of(summary));

            // Verify the key exists initially
            assertThat(redisTemplate.hasKey(key)).isTrue();

            // Wait up to 5 seconds for Redis to expire the key
            long deadline = System.nanoTime()
                    + TimeUnit.SECONDS.toNanos(5);

            while (Boolean.TRUE.equals(redisTemplate.hasKey(key))
                    && System.nanoTime() < deadline) {
                Thread.sleep(100);
            }

            // Verify the key has expired
            assertThat(redisTemplate.hasKey(key)).isFalse();

            // Verify cache-aside will see a cache miss
            assertThat(showCache.getShows(movieId)).isEmpty();

        } finally {
            // Clean up
            redisTemplate.delete(key);
        }
    }

    @Test
    void shouldEvictCachedShowsForMovie() {
        

        Long movieId = System.nanoTime();
        String key = "shows:movie:" + movieId; 
        
        try {
            ShowSummary summary = new ShowSummary();
            summary.setShowId(101L);
            summary.setMovieName("Interstellar");
            summary.setTheaterName("PVR");
            summary.setScreenName("Screen 1");

            OffsetDateTime startTime = OffsetDateTime.now();
            summary.setStartTime(startTime);

            List<ShowSummary> summaries = List.of(summary);

            // Act: store the summaries in Redis
            showCache.putShows(movieId, summaries);

            // Assert: Redis key exists
            assertThat(redisTemplate.hasKey(key)).isTrue();

            // Assert: cached data can be retrieved
            assertThat(showCache.getShows(movieId))
                .isPresent();

            // Act: evict the cache entry
            showCache.evictShows(movieId);

            // Assert 3: Redis key no longer exists
            assertThat(redisTemplate.hasKey(key)).isFalse();

            // Assert 4: cache now returns a miss
            assertThat(showCache.getShows(movieId))
                    .isEmpty();

        }
        
        finally {
            redisTemplate.delete(key);
        }
    }

}