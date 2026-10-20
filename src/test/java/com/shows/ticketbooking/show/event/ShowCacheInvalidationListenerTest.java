package com.shows.ticketbooking.show.event;

import com.shows.ticketbooking.infrastructure.redis.ShowCache;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

@SpringBootTest
class ShowCacheInvalidationListenerTest {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @MockitoBean
    private ShowCache showCache;

    @Test
    void shouldEvictCacheAfterTransactionCommits() {
        TransactionTemplate transaction =
                new TransactionTemplate(transactionManager);

        transaction.executeWithoutResult(status ->
                eventPublisher.publishEvent(
                        new ShowDataChangedEvent(42L)
                )
        );

        verify(showCache).evictShows(42L);
    }

    @Test
    void shouldNotEvictCacheAfterTransactionRollsBack() {
        TransactionTemplate transaction =
                new TransactionTemplate(transactionManager);

        transaction.executeWithoutResult(status -> {
            eventPublisher.publishEvent(
                    new ShowDataChangedEvent(43L)
            );

            status.setRollbackOnly();
        });

        verify(showCache, never()).evictShows(43L);
    }

    @Test
    void shouldNotFailWhenCacheEvictionFails() {
        doThrow(new DataAccessResourceFailureException("Redis unavailable"))
                .when(showCache).evictShows(44L);

        TransactionTemplate transaction =
                new TransactionTemplate(transactionManager);

        assertDoesNotThrow(() ->
                transaction.executeWithoutResult(status ->
                        eventPublisher.publishEvent(
                                new ShowDataChangedEvent(44L)
                        )
                )
        );

        verify(showCache).evictShows(44L);
    }
}