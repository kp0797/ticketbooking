package com.shows.ticketbooking.show.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.shows.ticketbooking.show.entity.Show;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    
}
