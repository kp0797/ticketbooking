package com.shows.ticketbooking.seat.service;

import com.shows.ticketbooking.TestcontainersConfiguration;
import com.shows.ticketbooking.catalog.entity.Movie;
import com.shows.ticketbooking.catalog.repository.MovieRepository;
import com.shows.ticketbooking.seat.entity.Seat;
import com.shows.ticketbooking.seat.repository.SeatRepository;
import com.shows.ticketbooking.show.entity.SeatStatus;
import com.shows.ticketbooking.show.entity.Show;
import com.shows.ticketbooking.show.entity.ShowSeat;
import com.shows.ticketbooking.show.repository.ShowRepository;
import com.shows.ticketbooking.show.repository.ShowSeatRepository;
import com.shows.ticketbooking.theater.entity.Screen;
import com.shows.ticketbooking.theater.entity.Theater;
import com.shows.ticketbooking.theater.repository.ScreenRepository;
import com.shows.ticketbooking.theater.repository.TheaterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ShowSeatServiceTest {

    @Autowired
    private ShowSeatService showSeatService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private SeatRepository seatRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private ShowSeatRepository showSeatRepository;

    @Test
    void shouldHoldShowSeat() {

        // Create Movie
        Movie movie = new Movie();
        movie.setMovieName("Interstellar");
        movie.setDurationMinutes(169);
        movie.setGenre("Sci-Fi");
        movie.setParentalRating("UA");
        movie.setLanguage("English");
        movie.setReleaseDate(LocalDate.of(2014, 11, 7));
        movie = movieRepository.save(movie);

        // Create Theater
        Theater theater = new Theater();
        theater.setName("PVR");
        theater.setCity("Hyderabad");
        theater.setAddress("Madhapur");
        theater = theaterRepository.save(theater);

        // Create Screen
        Screen screen = new Screen();
        screen.setTheater(theater);
        screen.setScreenName("Screen 1");
        screen.setScreenType("IMAX");
        screen = screenRepository.save(screen);

        // Create Seat
        Seat seat = new Seat();
        seat.setScreen(screen);
        seat.setRowLabel("A");
        seat.setSeatNumber(1);
        seat.setSeatType("REGULAR");
        seat = seatRepository.save(seat);

        // Create Show
        Show show = new Show();
        show.setMovie(movie);
        show.setScreen(screen);
        show.setStartTime(OffsetDateTime.now().plusHours(2));
        show.setEndTime(OffsetDateTime.now().plusHours(5));
        show = showRepository.save(show);

        // Create ShowSeat
        ShowSeat showSeat = new ShowSeat();
        showSeat.setShow(show);
        showSeat.setSeat(seat);
        showSeat.setStatus(SeatStatus.AVAILABLE);
        showSeat.setBasePrice(BigDecimal.valueOf(250));
        showSeat = showSeatRepository.save(showSeat);

        // Capture ID in a variable that is effectively final
        Long showSeatId = showSeat.getShowSeatId();

        // Verify initial state
        assertThat(showSeat.getStatus())
                .isEqualTo(SeatStatus.AVAILABLE);

        // Execute service method
        showSeatService.holdSeat(showSeatId);

        // Read again from database
        ShowSeat updatedShowSeat = showSeatRepository
                .findById(showSeatId)
                .orElseThrow();

        // Verify transaction rollback
        assertThat(updatedShowSeat.getStatus())
                .isEqualTo(SeatStatus.HELD);
    }
}

