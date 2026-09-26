package com.shows.ticketbooking.show.mapper;

import java.util.List;

import org.springframework.stereotype.Component;

import com.shows.ticketbooking.show.dto.ShowSummary;
import com.shows.ticketbooking.show.entity.Show;

@Component
public class ShowSummaryMapper {
    
    public ShowSummary toSummary(Show show) {
        ShowSummary showSummary = new ShowSummary();
        showSummary.setShowId(show.getShowId());
        showSummary.setMovieName(show.getMovie().getMovieName());
        showSummary.setScreenName(show.getScreen().getScreenName());
        showSummary.setTheaterName(show.getScreen().getTheater().getName());
        showSummary.setStartTime(show.getStartTime());
        return showSummary;
    }

    public List<ShowSummary> toSummaryList(List<Show> shows) {
        return shows.stream()
                .map(this::toSummary)
                .toList();
    }

}
