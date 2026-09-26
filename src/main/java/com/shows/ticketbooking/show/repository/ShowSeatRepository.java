package com.shows.ticketbooking.show.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shows.ticketbooking.show.entity.SeatStatus;
import com.shows.ticketbooking.show.entity.ShowSeat;




@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {
    
    // List<ShowSeat> findByShow_ShowIdAndStatus(
    //     Long showId,
    //     SeatStatus status
    // );

    @Query( 
        """
            SELECT s
            FROM ShowSeat s
            WHERE s.show.showId = :showId 
              AND s.status = :status
            ORDER BY s.seat.rowLabel ASC, s.seat.seatNumber ASC       
        """) 
    List<ShowSeat> findAvailableSeat(
        @Param("showId") Long showId,
        @Param("status") SeatStatus status
    );

    List<ShowSeat> findByShow_ShowId(Long showId);

    List<ShowSeat> findByShowSeatIdIn(List<Long> showSeatIds);
}

