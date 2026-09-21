package com.shows.ticketbooking.booking.entity;

import com.shows.ticketbooking.show.entity.ShowSeat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(
    name = "booking_seats",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_booking_show_seat",
            columnNames = {"booking_id", "show_seat_id"}
        )
    }
)
@Getter
@Setter
@NoArgsConstructor
public class BookingSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_seat_id")
    private Long bookingSeatId;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "show_seat_id", nullable = false)
    private ShowSeat showSeat;

    @NotNull
    @Column(
        name = "price_at_booking",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal priceAtBooking;

    @NotNull
    @Column(
        name = "discount_amount",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal discountAmount;

    @NotNull
    @Column(
        name = "final_price",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal finalPrice;
}