package com.shows.ticketbooking.show.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.shows.ticketbooking.show.entity.ShowSeat;




@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {
    
    List<ShowSeat> findByShow_ShowIdAndStatus(
        Long showId,
        ShowSeat status
    );
}
