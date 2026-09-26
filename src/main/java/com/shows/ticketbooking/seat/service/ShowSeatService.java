package com.shows.ticketbooking.seat.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.shows.ticketbooking.show.entity.SeatStatus;
import com.shows.ticketbooking.show.entity.ShowSeat;
import com.shows.ticketbooking.show.repository.ShowSeatRepository;

@Service
public class ShowSeatService {
    
    private final ShowSeatRepository showSeatRepository;

    public ShowSeatService(ShowSeatRepository showSeatRepository) {
        this.showSeatRepository = showSeatRepository;
    }

    @Transactional
    public void holdSeat(Long showSeatId) {

        ShowSeat showSeat = showSeatRepository.findById(showSeatId)
                            .orElseThrow(() -> new IllegalArgumentException("Show Seat Not found" + showSeatId));
        showSeat.setStatus(SeatStatus.HELD); 
        
    }
}
