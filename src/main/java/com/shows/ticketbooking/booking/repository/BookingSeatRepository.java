package com.shows.ticketbooking.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.shows.ticketbooking.booking.entity.BookingSeat;


@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    
}
