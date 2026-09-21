package com.shows.ticketbooking.seat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.shows.ticketbooking.seat.entity.Seat;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    
}
