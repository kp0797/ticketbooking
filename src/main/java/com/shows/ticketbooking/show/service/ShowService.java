package com.shows.ticketbooking.show.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.shows.ticketbooking.infrastructure.redis.ShowCache;
import com.shows.ticketbooking.show.dto.ShowSummary;
import com.shows.ticketbooking.show.entity.Show;
import com.shows.ticketbooking.show.mapper.ShowSummaryMapper;
import com.shows.ticketbooking.show.repository.ShowRepository;

@Service
public class ShowService {
    
    private final ShowRepository showRepository;
    private final ShowSummaryMapper showSummaryMapper;
    private final ShowCache showCache;

    public ShowService(ShowRepository showRepository, ShowSummaryMapper showSummaryMapper, ShowCache showCache) {
        this.showRepository = showRepository;
        this.showSummaryMapper = showSummaryMapper;
        this.showCache = showCache;
    }

    public List<ShowSummary> getAllUpcomingShows(Long movieId, OffsetDateTime startTime) {

        // Check in cache
        Optional<List<ShowSummary>> cachedShows = showCache.getShows(movieId);

        if(cachedShows.isPresent()) {
            return cachedShows.get();
        }

        // Check DB
        List<Show> shows = showRepository.findByMovie_MovieIdAndStartTimeAfter(movieId, startTime);
        List<ShowSummary> showSummaries =  showSummaryMapper.toSummaryList(shows);

        // Put into redis
        showCache.putShows(movieId, showSummaries);

        return showSummaries;
    }
       
    
}
