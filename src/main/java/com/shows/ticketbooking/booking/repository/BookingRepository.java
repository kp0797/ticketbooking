package com.shows.ticketbooking.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.Entity;

public interface BookingRepository extends JpaRepository<Entity, Long>{
    
}
