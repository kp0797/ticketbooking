package com.shows.ticketbooking.catalog.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.Entity;

public interface MovieRepository extends JpaRepository<Entity, Long> {
    
}
