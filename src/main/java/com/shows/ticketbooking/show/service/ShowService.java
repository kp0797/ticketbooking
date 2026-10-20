package com.shows.ticketbooking.show.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shows.ticketbooking.infrastructure.redis.ShowCache;
import com.shows.ticketbooking.show.dto.ShowSummary;
import com.shows.ticketbooking.show.entity.Show;
import com.shows.ticketbooking.show.event.ShowDataChangedEvent;
import com.shows.ticketbooking.show.mapper.ShowSummaryMapper;
import com.shows.ticketbooking.show.repository.ShowRepository;

@Service
public class ShowService {
    
    private final ShowRepository showRepository;
    private final ShowSummaryMapper showSummaryMapper;
    private final ShowCache showCache;
    private final ApplicationEventPublisher applicationEventPublisher;

    public ShowService(ShowRepository showRepository, ShowSummaryMapper showSummaryMapper, ShowCache showCache, ApplicationEventPublisher applicationEventPublisher) {
        this.showRepository = showRepository;
        this.showSummaryMapper = showSummaryMapper;
        this.showCache = showCache;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public List<ShowSummary> getAllUpcomingShows(Long movieId) {

        // Check in cache
        Optional<List<ShowSummary>> cachedShows = showCache.getShows(movieId);

        // Cache Hit
        if(cachedShows.isPresent()) {
            return cachedShows.get();
        }

        OffsetDateTime startTime = OffsetDateTime.now();

        // Cache Miss Check DB
        List<Show> shows = showRepository.findByMovie_MovieIdAndStartTimeAfter(movieId, startTime);
        List<ShowSummary> showSummaries =  showSummaryMapper.toSummaryList(shows);

        // Put into redis
        showCache.putShows(movieId, showSummaries);

        return showSummaries;
    }

    @Transactional
    public Show createShow(Show show) {
        Show savedShow = showRepository.save(show);

        applicationEventPublisher.publishEvent(
            new ShowDataChangedEvent(savedShow.getMovie().getMovieId())
        );

        return savedShow;
             
    }
       
    
}
