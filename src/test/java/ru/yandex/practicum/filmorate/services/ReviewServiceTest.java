package ru.yandex.practicum.filmorate.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.impl.*;

import java.util.List;
import java.util.Optional;

@SpringBootTest
class ReviewServiceTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private ReviewStorage reviewStorage;
    private ReviewUserStorage reviewUserStorage;
    private UserStorage userStorage;
    private FilmStorage filmStorage;
    private FilmGenreStorage filmGenreStorage;
    private EventStorage eventStorage;
    private GenreStorage genreStorage;
    private MpaStorage mpaStorage;
    private DirectorStorage directorStorage;
    private FilmDirectorStorage filmDirectorStorage;
    private ReviewService reviewService;


    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(embeddedDatabase);
        reviewUserStorage = new ReviewUserDbStorage(jdbcTemplate);
        mpaStorage = new MpaDbStorage(jdbcTemplate);
        genreStorage = new GenreDbStorage(jdbcTemplate);
        reviewStorage = new ReviewDbStorage(jdbcTemplate, reviewUserStorage);
        eventStorage = new EventDbStorage(jdbcTemplate);
        filmGenreStorage = new FilmGenreDbStorage(jdbcTemplate, namedParameterJdbcTemplate);
        directorStorage = new DirectorDbStorage(jdbcTemplate);
        filmDirectorStorage = new FilmDirectorDbStorage(jdbcTemplate, namedParameterJdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, mpaStorage, filmGenreStorage,
                genreStorage, directorStorage, filmDirectorStorage);
        userStorage = new UserDbStorage(jdbcTemplate, filmStorage);
        reviewService = new ReviewService(reviewStorage, reviewUserStorage,
                userStorage, filmStorage, eventStorage);

    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void findAllReviews_Normal_AllFilms_Limit10() {
        List<Review> reviews = reviewService.findAllReviews(null, 10);

        Assertions.assertThat(reviews)
                .hasSize(2);
    }

    @Test
    void findAllReviews_Normal_Films_Limit10() {
        List<Review> reviews = reviewService.findAllReviews(1L, 10);

        Assertions.assertThat(reviews)
                .hasSize(1);
    }

    @Test
    void findReviewById_Normal() {
        Review review = reviewService.findReviewById(1L);

        Assertions.assertThat(review)
                .isNotNull()
                .hasFieldOrPropertyWithValue("reviewId", 1L)
                .hasFieldOrPropertyWithValue("content", "This film is sooo bad.");
    }

    @Test
    void findReviewById_WithWrongId() {
        Throwable thrown = Assertions.catchException(() -> reviewService.findReviewById(999L));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Отзыв с id=%d не существует.", 999));
    }

    @Test
    void createReview_Normal() {
        Review newReview = Review.builder()
                .content("good film")
                .isPositive(true)
                .userId(3L)
                .filmId(3L)
                .build();
        Review added = reviewService.createReview(newReview);

        List<Review> reviews = reviewService.findAllReviews(null, 10);
        Assertions.assertThat(reviews)
                .hasSize(3)
                .contains(added);
    }

    @Test
    void updateReview_Normal() {
        Review review = reviewService.findReviewById(1L);

        review.setContent("bad film");
        review.setIsPositive(false);
        review.setUserId(3L);
        review.setFilmId(3L);

        reviewService.updateReview(review);

        Review reviewUpdated = reviewService.findReviewById(1L);

        Assertions.assertThat(reviewUpdated)
                .hasFieldOrPropertyWithValue("content", "bad film")
                .hasFieldOrPropertyWithValue("isPositive", false)
                .hasFieldOrPropertyWithValue("userId", 1L)
                .hasFieldOrPropertyWithValue("filmId", 1L);
    }

    @Test
    void deleteReview_Normal() {
        reviewService.deleteReview(1L);

        Throwable thrown = Assertions.catchException(() -> reviewService.findReviewById(1L));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Отзыв с id=%d не существует.", 1));

        String sql = "SELECT COUNT(*) AS count FROM review_user WHERE review_id=?";
        List<Integer> results = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("count"), 1);
        Optional<Integer> count = results.size() == 0 ? Optional.empty() : Optional.of(results.get(0));

        Assertions.assertThat(count)
                .isPresent()
                .hasValue(0);
    }

    @Test
    void likeReview_Normal() {
        reviewService.likeReview(1L, 2L);

        Review review = reviewService.findReviewById(1L);

        Assertions.assertThat(review)
                .hasFieldOrPropertyWithValue("useful", 3L);
    }

    @Test
    void dislikeReview_Normal() {
        reviewService.dislikeReview(1L, 2L);

        Review review = reviewService.findReviewById(1L);

        Assertions.assertThat(review)
                .hasFieldOrPropertyWithValue("useful", 1L);
    }

    @Test
    void deleteLikeDislikeReview_Normal() {
        reviewService.deleteLikeDislikeReview(1L, 3L);

        Review review = reviewService.findReviewById(1L);

        Assertions.assertThat(review)
                .hasFieldOrPropertyWithValue("useful", 1L);
    }
}