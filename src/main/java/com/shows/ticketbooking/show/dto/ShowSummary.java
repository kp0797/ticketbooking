package com.shows.ticketbooking.show.dto;

import java.time.OffsetDateTime;

import lombok.Data;

@Data
public class ShowSummary {
    
    private Long showId;
    private String movieName;
    private String theaterName;
    private String screenName;
    private OffsetDateTime startTime;

}
