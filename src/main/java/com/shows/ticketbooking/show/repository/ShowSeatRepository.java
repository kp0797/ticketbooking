package com.shows.ticketbooking.show.repository;

import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.shows.ticketbooking.show.entity.SeatStatus;
import com.shows.ticketbooking.show.entity.ShowSeat;




@Repository
public interface ShowSeatRepository extends JpaRepository<ShowSeat, Long> {
    
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

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s from ShowSeat s where s.showSeatId = :showSeatId")
    Optional<ShowSeat> findByIdForUpdate (
        @Param("showSeatId") Long showSeatId
    );
}

