package com.shows.ticketbooking.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.shows.ticketbooking.catalog.entity.Movie;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    
}
