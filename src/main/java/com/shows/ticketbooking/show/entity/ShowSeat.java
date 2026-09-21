package com.shows.ticketbooking.show.entity;

import com.shows.ticketbooking.seat.entity.Seat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
    name = "show_seats",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_show_seat",
            columnNames = {"show_id", "seat_id"}
        )
    }
)

@Getter
@Setter
@NoArgsConstructor
public class ShowSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "show_seat_id")
    private Long showSeatId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "seat_id", nullable = false)
    private Seat seat;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SeatStatus status;

    @NotNull
    @Column(name = "base_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal basePrice;
}