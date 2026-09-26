package com.shows.ticketbooking.show.service;

import com.shows.ticketbooking.TestcontainersConfiguration;
import com.shows.ticketbooking.catalog.entity.Movie;
import com.shows.ticketbooking.catalog.repository.MovieRepository;
import com.shows.ticketbooking.show.dto.ShowSummary;
import com.shows.ticketbooking.show.entity.Show;
import com.shows.ticketbooking.theater.entity.Screen;
import com.shows.ticketbooking.theater.entity.Theater;
import com.shows.ticketbooking.theater.repository.ScreenRepository;
import com.shows.ticketbooking.theater.repository.TheaterRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ShowServiceTest {

    @Autowired
    private ShowService showService;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private TheaterRepository theaterRepository;

    @Autowired
    private ScreenRepository screenRepository;

    @Autowired
    private com.shows.ticketbooking.show.repository.ShowRepository showRepository;

    @Test
    void shouldReturnOnlyUpcomingShowsForMovie() {

        OffsetDateTime now = OffsetDateTime.now();

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

        // Past show
        Show pastShow = new Show();
        pastShow.setMovie(movie);
        pastShow.setScreen(screen);
        pastShow.setStartTime(now.minusDays(2));
        pastShow.setEndTime(now.minusDays(2).plusHours(3));

        pastShow = showRepository.save(pastShow);

        // Upcoming show 1
        Show upcomingShow1 = new Show();
        upcomingShow1.setMovie(movie);
        upcomingShow1.setScreen(screen);
        upcomingShow1.setStartTime(now.plusDays(1));
        upcomingShow1.setEndTime(now.plusDays(1).plusHours(3));

        upcomingShow1 = showRepository.save(upcomingShow1);

        // Upcoming show 2
        Show upcomingShow2 = new Show();
        upcomingShow2.setMovie(movie);
        upcomingShow2.setScreen(screen);
        upcomingShow2.setStartTime(now.plusDays(2));
        upcomingShow2.setEndTime(now.plusDays(2).plusHours(3));

        upcomingShow2 = showRepository.save(upcomingShow2);

        // Execute service
        List<ShowSummary> result =
                showService.getAllUpcomingShows(movie.getMovieId(), now);

        // Verify count
        assertThat(result)
                .hasSize(2);

        // Verify past show is not returned
        assertThat(result)
                .extracting(ShowSummary::getShowId)
                .doesNotContain(pastShow.getShowId());

        // Verify both upcoming shows are returned
        assertThat(result)
                .extracting(ShowSummary::getShowId)
                .containsExactlyInAnyOrder(
                        upcomingShow1.getShowId(),
                        upcomingShow2.getShowId()
                );

        // Verify mapping
        assertThat(result)
                .allSatisfy(showSummary -> {
                    assertThat(showSummary.getMovieName())
                            .isEqualTo("Interstellar");

                    assertThat(showSummary.getTheaterName())
                            .isEqualTo("PVR");

                    assertThat(showSummary.getScreenName())
                            .isEqualTo("Screen 1");
                });
    }
}