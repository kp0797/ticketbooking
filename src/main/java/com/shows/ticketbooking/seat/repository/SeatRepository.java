package com.shows.ticketbooking.seat.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.Entity;

public interface SeatRepository extends JpaRepository<Entity, Long> {
    
}
