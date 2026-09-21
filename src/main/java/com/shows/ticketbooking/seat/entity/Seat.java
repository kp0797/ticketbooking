package com.shows.ticketbooking.seat.entity;

import com.shows.ticketbooking.theater.entity.Screen;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "seats",
       uniqueConstraints = {
        @UniqueConstraint (
            name = "uk_screen_seat",
            columnNames = {"screen_id", "row_label", "seat_number"}
        )
       })
@Getter
@Setter
@NoArgsConstructor
public class Seat {

    @Id
    @Column(name = "seat_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @NotBlank
    @Column(name = "row_label", nullable = false, length = 2)
    private String rowLabel;

    @NotNull
    @Column(name = "seat_number", nullable = false)
    private Integer seatNumber;

    @NotBlank
    @Column(name = "seat_type", nullable = false, length = 20)
    private String seatType;
}
