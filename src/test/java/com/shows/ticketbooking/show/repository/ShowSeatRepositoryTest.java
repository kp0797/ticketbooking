package com.shows.ticketbooking.show.repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.shows.ticketbooking.catalog.entity.Movie;
import com.shows.ticketbooking.catalog.repository.MovieRepository;
import com.shows.ticketbooking.seat.entity.Seat;
import com.shows.ticketbooking.seat.repository.SeatRepository;
import com.shows.ticketbooking.show.entity.SeatStatus;
import com.shows.ticketbooking.show.entity.Show;
import com.shows.ticketbooking.show.entity.ShowSeat;
import com.shows.ticketbooking.theater.entity.Screen;
import com.shows.ticketbooking.theater.entity.Theater;
import com.shows.ticketbooking.theater.repository.ScreenRepository;
import com.shows.ticketbooking.theater.repository.TheaterRepository;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Import(com.shows.ticketbooking.TestcontainersConfiguration.class)
public class ShowSeatRepositoryTest {
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
    void findAvailableSeatForShows() {

        //Movie
        Movie movie = new Movie();
        movie.setMovieName("Transformers");
        movie.setLanguage("English");
        movie.setParentalRating("U/A");
        movie.setDurationMinutes(120);
        movie.setGenre("Sci-Fi");
        movie.setReleaseDate(LocalDate.of(2026, 9, 21));
        movie = movieRepository.save(movie);

        //Theater
        Theater theater = new Theater();
        theater.setName("PVR");
        theater.setCity("Hyderabad");
        theater.setAddress("Hyderabad, Telangana");
        theater = theaterRepository.save(theater);

        //Screen
        Screen screen = new Screen();
        screen.setTheater(theater);
        screen.setScreenName("Screen 1");
        screen.setScreenType("IMAX");
        screen = screenRepository.save(screen);
        
        //Seat
        Seat seat1 = createSeat(screen, "A", 1);
        Seat seat2 = createSeat(screen, "A", 2);
        Seat seat3 = createSeat(screen, "A", 3);

        seat1 = seatRepository.save(seat1);
        seat2 = seatRepository.save(seat2);
        seat3 = seatRepository.save(seat3);

        // Show
        Show show = new Show();
        show.setMovie(movie);
        show.setScreen(screen);
        show.setStartTime(OffsetDateTime.now().plusHours(2));
        show.setEndTime(OffsetDateTime.now().plusHours(5));
        show = showRepository.save(show);

        // ShowSeats
        ShowSeat showSeat1 = createShowSeat(show, seat1, SeatStatus.AVAILABLE);
        ShowSeat showSeat2 = createShowSeat(show, seat2, SeatStatus.HELD);
        ShowSeat showSeat3 = createShowSeat(show, seat3, SeatStatus.AVAILABLE);

        showSeatRepository.saveAll(List.of(showSeat1, showSeat2, showSeat3));

        // Execute query
        List<ShowSeat> result =
                showSeatRepository.findAvailableSeat(
                        show.getShowId(),
                        SeatStatus.AVAILABLE
                );

        // Verify
        assertThat(result).hasSize(2);

        assertThat(result)
                .extracting(ShowSeat::getSeat)
                .extracting(Seat::getSeatNumber)
                .containsExactlyInAnyOrder(1, 3);
    }
    

    private Seat createSeat(Screen screen, String row, int number) {
        Seat seat = new Seat();
        seat.setScreen(screen);
        seat.setRowLabel(row);
        seat.setSeatNumber(number);
        seat.setSeatType("REGULAR");
        return seat;
    }

    private ShowSeat createShowSeat(
            Show show,
            Seat seat,
            SeatStatus status
    ) {
        ShowSeat showSeat = new ShowSeat();
        showSeat.setShow(show);
        showSeat.setSeat(seat);
        showSeat.setStatus(status);
        showSeat.setBasePrice(BigDecimal.valueOf(250));
        return showSeat;
    }

}
