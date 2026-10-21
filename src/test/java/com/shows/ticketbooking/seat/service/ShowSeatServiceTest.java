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
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.concurrent.*;

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

    private ShowSeat createAvailableShowSeat() {
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

        return showSeat;
    }

    @Test
    void shouldHoldShowSeat() {

        ShowSeat showSeat = createAvailableShowSeat();
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

        // Verify HoldToken and HoldExpiresAt
        assertThat(updatedShowSeat.getHoldToken()).isNotNull();

        assertThat(updatedShowSeat.getHoldExpiresAt()).isAfter(OffsetDateTime.now());

        // Verify transaction rollback
        assertThat(updatedShowSeat.getStatus())
                .isEqualTo(SeatStatus.HELD);

        assertThatThrownBy(() -> showSeatService.holdSeat(showSeatId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Seat not available to Book");

        // The seat must remain HELD
        ShowSeat finalSeat = showSeatRepository.findById(showSeatId)
                .orElseThrow();

        assertThat(finalSeat.getStatus()).isEqualTo(SeatStatus.HELD);
    }

    @Test
    void shouldAllowOnlyOneConcurrentHoldForSameSeat() throws Exception {

        Long showSeatId = createAvailableShowSeat().getShowSeatId();

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);

        try {
            Future<Boolean> first = executor.submit(() -> {
                ready.countDown();
                start.await();

                try {
                    showSeatService.holdSeat(showSeatId);
                    return true;
                } catch (IllegalStateException ex) {
                    return false;
                }
            });

            Future<Boolean> second = executor.submit(() -> {
                ready.countDown();
                start.await();

                try {
                    showSeatService.holdSeat(showSeatId);
                    return true;
                } catch (IllegalStateException ex) {
                    return false;
                }
            });

            // Wait until both threads are ready
            assertThat(ready.await(5, TimeUnit.SECONDS)).isTrue();

            // Release both threads together
            start.countDown();

            boolean firstSucceeded = first.get(10, TimeUnit.SECONDS);
            boolean secondSucceeded = second.get(10, TimeUnit.SECONDS);

            // Exactly one request should succeed
            assertThat(firstSucceeded).isNotEqualTo(secondSucceeded);

            // The seat must end up HELD
            ShowSeat persistedSeat = showSeatRepository
                    .findById(showSeatId)
                    .orElseThrow();

            assertThat(persistedSeat.getStatus())
                    .isEqualTo(SeatStatus.HELD);

        } finally {
            start.countDown();
            executor.shutdownNow();
        }
    }

    @Test
    void shouldReclaimExpiredHoldAndReplaceToken() {

        ShowSeat showSeat = createAvailableShowSeat();

        UUID oldToken = UUID.randomUUID();
        OffsetDateTime expiredAt = OffsetDateTime.now().minusMinutes(1);

        // Simulate a previous hold that has expired
        showSeat.setStatus(SeatStatus.HELD);
        showSeat.setHoldToken(oldToken);
        showSeat.setHoldExpiresAt(expiredAt);

        showSeatRepository.saveAndFlush(showSeat);

        Long showSeatId = showSeat.getShowSeatId();

        // Reclaim the seat
        UUID newToken = showSeatService.holdSeat(showSeatId);

        // Verify the persisted result
        ShowSeat reclaimedSeat = showSeatRepository
                .findById(showSeatId)
                .orElseThrow();

        assertThat(newToken)
                .isNotNull()
                .isNotEqualTo(oldToken);

        assertThat(reclaimedSeat.getStatus())
                .isEqualTo(SeatStatus.HELD);

        assertThat(reclaimedSeat.getHoldToken())
                .isEqualTo(newToken);

        assertThat(reclaimedSeat.getHoldExpiresAt())
                .isAfter(OffsetDateTime.now());
    }

    @Test
    void shouldRejectHoldingBookedSeat() {

        ShowSeat showSeat = createAvailableShowSeat();
        showSeat.setStatus(SeatStatus.BOOKED);
        showSeatRepository.saveAndFlush(showSeat);

        Long showSeatId = showSeat.getShowSeatId();

        assertThatThrownBy(() -> showSeatService.holdSeat(showSeatId))
                .isInstanceOf(IllegalStateException.class);

        ShowSeat persistedSeat = showSeatRepository
                .findById(showSeatId)
                .orElseThrow();

        assertThat(persistedSeat.getStatus())
                .isEqualTo(SeatStatus.BOOKED);
    }

    @Test
    void shouldRejectHoldWhenShowSeatDoesNotExist() {

        Long nonExistingShowSeatId = Long.MAX_VALUE;

        assertThatThrownBy(
                () -> showSeatService.holdSeat(nonExistingShowSeatId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Show Seat Not found");
    }

}

