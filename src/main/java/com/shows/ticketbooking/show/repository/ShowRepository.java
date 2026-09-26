package com.shows.ticketbooking.show.repository;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.shows.ticketbooking.show.entity.Show;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    
    List<Show> findByMovie_MovieIdAndStartTimeAfter(
            Long movieId,
            OffsetDateTime startTime
    );

}
