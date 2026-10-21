package com.shows.ticketbooking.seat.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shows.ticketbooking.show.entity.SeatStatus;
import com.shows.ticketbooking.show.entity.ShowSeat;
import com.shows.ticketbooking.show.repository.ShowSeatRepository;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class ShowSeatService {
    
    private final ShowSeatRepository showSeatRepository;
    private final Duration holdTtl;

    public ShowSeatService(ShowSeatRepository showSeatRepository, @Value("${app.booking.seat-hold-ttl:5m}") Duration holdTtl) {
        this.showSeatRepository = showSeatRepository;
        this.holdTtl = holdTtl;
    }

    @Transactional
    public UUID holdSeat(Long showSeatId) {

        ShowSeat showSeat = showSeatRepository.findByIdForUpdate(showSeatId)
                            .orElseThrow(() -> new IllegalArgumentException("Show Seat Not found" + showSeatId));

        OffsetDateTime now = OffsetDateTime.now();
        boolean isAvailable = showSeat.getStatus() == SeatStatus.AVAILABLE;
        boolean isExpiredHold = showSeat.getStatus() == SeatStatus.HELD
                                && showSeat.getHoldExpiresAt() != null
                                && !showSeat.getHoldExpiresAt().isAfter(now);

        if(!isAvailable && !isExpiredHold) {
            throw new IllegalStateException("Seat not available to Book" + showSeatId);
        }

        UUID holdToken = UUID.randomUUID();
        showSeat.setStatus(SeatStatus.HELD);
        showSeat.setHoldToken(holdToken);
        showSeat.setHoldExpiresAt(now.plus(holdTtl));

        return holdToken;
        
    }
}
