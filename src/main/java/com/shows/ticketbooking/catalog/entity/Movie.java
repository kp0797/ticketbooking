package com.shows.ticketbooking.catalog.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "movies")
public class Movie {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "movie_id")
    private Long movieId;

    @NotBlank
    @Column(name = "movie_name", nullable = false, length = 200)
    private String movieName;

    @NotNull
    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;
    
    @NotBlank
    @Column(name = "genre", nullable = false, length=50)
    private String genre;
    
    @NotBlank
    @Column(name = "parental_rating", nullable = false, length = 10)
    private String parentalRating;
    
    @NotBlank
    @Column(name = "language", nullable = false, length = 50)
    private String language;

    @NotNull
    @Column(name = "release_date", nullable = false)
    private LocalDate releaseDate;
    
}
