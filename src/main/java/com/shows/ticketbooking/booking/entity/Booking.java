package com.shows.ticketbooking.booking.entity;

import com.shows.ticketbooking.show.entity.Show;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long bookingId;

    @NotBlank
    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "show_id", nullable = false)
    private Show show;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "booking_status", nullable = false, length = 30)
    private BookingStatus bookingStatus;

    @NotNull
    @Column(
        name = "total_amount",
        nullable = false,
        precision = 12,
        scale = 2
    )
    private BigDecimal totalAmount;

    @NotNull
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;
}