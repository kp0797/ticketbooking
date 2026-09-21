package com.shows.ticketbooking.show.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.Entity;

public interface ShowRepository extends JpaRepository<Entity, Long> {
    
}
