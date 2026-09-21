package com.shows.ticketbooking.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import jakarta.persistence.Entity;

public interface PaymentRepository extends JpaRepository<Entity, Long> {
    
}
