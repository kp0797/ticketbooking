package com.shows.ticketbooking.theater.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.shows.ticketbooking.theater.entity.Theater;


@Repository
public interface TheaterRepository extends JpaRepository<Theater, Long>{
    
}
