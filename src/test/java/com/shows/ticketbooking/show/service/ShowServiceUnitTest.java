package com.shows.ticketbooking.show.service;

import com.shows.ticketbooking.catalog.entity.Movie;
import com.shows.ticketbooking.infrastructure.redis.ShowCache;
import com.shows.ticketbooking.show.dto.ShowSummary;
import com.shows.ticketbooking.show.entity.Show;
import com.shows.ticketbooking.show.event.ShowDataChangedEvent;
import com.shows.ticketbooking.show.mapper.ShowSummaryMapper;
import com.shows.ticketbooking.show.repository.ShowRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShowServiceUnitTest {

    @Mock
    private ShowRepository showRepository;

    @Mock
    private ShowSummaryMapper showSummaryMapper;

    @Mock
    private ShowCache showCache;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @InjectMocks
    private ShowService showService;

    @Test
    void shouldReturnCachedShowsWithoutCallingDatabase() {

        Long movieId = 1L;
        OffsetDateTime startTime = OffsetDateTime.now();

        ShowSummary showSummary = new ShowSummary();
        showSummary.setShowId(101L);
        showSummary.setMovieName("Interstellar");
        showSummary.setTheaterName("PVR");
        showSummary.setScreenName("Screen 1");
        showSummary.setStartTime(startTime);

        List<ShowSummary> cachedShows = List.of(showSummary);

        // Mock cache hit
        when(showCache.getShows(movieId))
                .thenReturn(Optional.of(cachedShows));

        // Execute
        List<ShowSummary> result =
                showService.getAllUpcomingShows(movieId);

        // Verify result
        assertThat(result)
                .containsExactlyElementsOf(cachedShows);

        // Verify cache was checked
        verify(showCache)
                .getShows(movieId);

        // Verify DB was NOT called
        verify(showRepository, never())
                .findByMovie_MovieIdAndStartTimeAfter(movieId, startTime);

        // Verify mapper was NOT called
        verify(showSummaryMapper, never())
                .toSummaryList(org.mockito.ArgumentMatchers.anyList());

        // Verify Cache is not written again
        verify(showCache, never())
                .putShows(movieId, cachedShows);
    }

    @Test
    void shouldFetchFromDatabaseAndCacheWhenCacheMisses() {

        Long movieId = 1L;
        OffsetDateTime startTime = OffsetDateTime.now();

        // Simulate a Show entity returned by the repository
        Show show = mock(Show.class);
        List<Show> databaseShows = List.of(show);

        // Expected DTO returned by the mapper
        ShowSummary showSummary = new ShowSummary();
        showSummary.setShowId(101L);
        showSummary.setMovieName("Interstellar");
        showSummary.setTheaterName("PVR");
        showSummary.setScreenName("Screen 1");
        showSummary.setStartTime(startTime);

        List<ShowSummary> mappedSummaries = List.of(showSummary);

        // Arrange: cache miss
        when(showCache.getShows(movieId))
                .thenReturn(Optional.empty());

        // Arrange: database returns shows
        when(showRepository.findByMovie_MovieIdAndStartTimeAfter(
                org.mockito.ArgumentMatchers.eq(movieId), 
                org.mockito.ArgumentMatchers.any(OffsetDateTime.class)))
                .thenReturn(databaseShows);

        // Arrange: mapper converts entities to DTOs
        when(showSummaryMapper.toSummaryList(databaseShows))
                .thenReturn(mappedSummaries);

        // Act
        List<ShowSummary> result =
                showService.getAllUpcomingShows(movieId);

        // Assert: return mapped results
        assertThat(result)
                .containsExactlyElementsOf(mappedSummaries);

        // Verify cache was checked
        verify(showCache).getShows(movieId);

        // Verify database was queried exactly once
        verify(showRepository)
                .findByMovie_MovieIdAndStartTimeAfter(
                        org.mockito.ArgumentMatchers.eq(movieId), 
                        org.mockito.ArgumentMatchers.any(OffsetDateTime.class));

        // Verify mapper was called exactly once
        verify(showSummaryMapper)
                .toSummaryList(databaseShows);

        // Verify mapped results were cached
        verify(showCache)
                .putShows(movieId, mappedSummaries);
    }

    @Test
    void createShow_shouldSaveShowAndPublishEvent() {
        Movie movie = new Movie();
        movie.setMovieId(42L);

        Show show = new Show();
        show.setMovie(movie);

        when(showRepository.save(show)).thenReturn(show);

        Show result = showService.createShow(show);

        assertSame(show, result);
        verify(showRepository).save(show);

        ArgumentCaptor<ShowDataChangedEvent> captor =
                ArgumentCaptor.forClass(ShowDataChangedEvent.class);

        verify(applicationEventPublisher).publishEvent(captor.capture());

        assertEquals(42L, captor.getValue().movieId());
   }
}
