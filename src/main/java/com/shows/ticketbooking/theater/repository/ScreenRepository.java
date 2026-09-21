package com.shows.ticketbooking.theater.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.Entity;

public interface ScreenRepository extends JpaRepository<Entity, Long> {
    
}
