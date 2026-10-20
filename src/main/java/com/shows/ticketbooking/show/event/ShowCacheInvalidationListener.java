package com.shows.ticketbooking.show.event;

import com.shows.ticketbooking.infrastructure.redis.ShowCache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ShowCacheInvalidationListener {

    private final ShowCache showCache;
    private static final Logger log = LoggerFactory.getLogger(ShowCacheInvalidationListener.class);

    public ShowCacheInvalidationListener(ShowCache showCache) {
        this.showCache = showCache;
    }

    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handleShowDataChanged(ShowDataChangedEvent event) {
        try {
            showCache.evictShows(event.movieId());
        } catch (DataAccessException ex) {
            log.warn("Failed to evict keys", event.movieId(),ex);
        }

    }
}