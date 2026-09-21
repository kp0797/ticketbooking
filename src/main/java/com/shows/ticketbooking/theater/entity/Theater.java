package com.shows.ticketbooking.theater.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "theaters")
@Getter
@Setter
@NoArgsConstructor
public class Theater {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theater_id")
    private Long theaterId;

    @NotBlank
    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @NotBlank
    @Column(name = "city", nullable = false, length = 50)
    private String city;

    @NotBlank
    @Column(name = "address", nullable = false, length = 500)
    private String address;
}
