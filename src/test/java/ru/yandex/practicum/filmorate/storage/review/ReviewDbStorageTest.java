package ru.yandex.practicum.filmorate.storage.review;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.ReviewUserStorage;
import ru.yandex.practicum.filmorate.storage.impl.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.ReviewUserDbStorage;

import java.util.List;
import java.util.Optional;

@SpringBootTest
class ReviewDbStorageTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private ReviewStorage reviewStorage;
    private ReviewUserStorage reviewUserStorage;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        reviewUserStorage = new ReviewUserDbStorage(jdbcTemplate);
        reviewStorage = new ReviewDbStorage(jdbcTemplate, reviewUserStorage);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void findAllReviews_AllFilms_Count10_WhereRecordIs2() {
        List<Review> reviews = reviewStorage.findAllReviews(10);

        Assertions.assertThat(reviews)
                .hasSize(2);
    }

    @Test
    void findAllReviewsByFilmId_FilmId1_Count10() {
        List<Review> reviews = reviewStorage.findAllReviewsByFilmId(1L, 10);

        Assertions.assertThat(reviews)
                .hasSize(1);
    }

    @Test
    void findAllReviewsByFilmId_WrongFilmId999_Count10() {
        List<Review> reviews = reviewStorage.findAllReviewsByFilmId(999L, 10);

        Assertions.assertThat(reviews)
                .hasSize(0);
    }

    @Test
    void findReviewById_Normal_ReviewId1() {
        Optional<Review> reviewOptional = reviewStorage.findReviewById(1L);
        Assertions.assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review ->
                        Assertions.assertThat(review).hasFieldOrPropertyWithValue("reviewId", 1L)
                );
    }

    @Test
    void findReviewById_WrongId999() {
        Optional<Review> reviewOptional = reviewStorage.findReviewById(999L);
        Assertions.assertThat(reviewOptional)
                .isNotPresent()
                .isEmpty();
    }

    @Test
    void checkReview_Normal() {
        Boolean check = reviewStorage.checkReview(1L);
        Assertions.assertThat(check).isTrue();
    }

    @Test
    void checkReview_WrongId999() {
        Boolean check = reviewStorage.checkReview(999L);
        Assertions.assertThat(check).isFalse();
    }

    @Test
    void createReview_Normal() {
        Review reviewCreate = Review.builder()
                .content("review content")
                .isPositive(true)
                .userId(3L)
                .filmId(3L)
                .build();

        Long id = reviewStorage.createReview(reviewCreate).getReviewId();

        Optional<Review> reviewOptional = reviewStorage.findReviewById(id);

        Assertions.assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        Assertions.assertThat(film)
                                .hasFieldOrPropertyWithValue("reviewId", 3L)
                                .hasFieldOrPropertyWithValue("content", "review content")
                                .hasFieldOrPropertyWithValue("isPositive", true)
                );
    }

    @Test
    void updateReview_Normal_OnlyContentAndIsPositive() {
        Review reviewUpdated = reviewStorage.findReviewById(1L).get();
        reviewUpdated.setIsPositive(false);
        reviewUpdated.setContent("new content");
        reviewUpdated.setUserId(99L);
        reviewUpdated.setFilmId(99L);

        reviewStorage.updateReview(reviewUpdated);

        Optional<Review> reviewOptional = reviewStorage.findReviewById(reviewUpdated.getReviewId());

        Assertions.assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review ->
                        Assertions.assertThat(review)
                                .hasFieldOrPropertyWithValue("reviewId", 1L)
                                .hasFieldOrPropertyWithValue("content", "new content")
                                .hasFieldOrPropertyWithValue("userId", 1L)
                                .hasFieldOrPropertyWithValue("filmId", 1L)
                );
    }

    @Test
    void updateReview_WrongId() {
        Review reviewUpdated = Review.builder()
                .reviewId(999L)
                .content("content")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .build();
        Throwable thrown = Assertions.catchException(() -> reviewStorage.updateReview(reviewUpdated));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Отзыва с id=%d не существует.", reviewUpdated.getReviewId()));
    }

    @Test
    void deleteReview_Normal() {

        reviewStorage.deleteReview(1L);

        Optional<Review> reviewOptional = reviewStorage.findReviewById(1L);
        Assertions.assertThat(reviewOptional)
                .isNotPresent()
                .isEmpty();
    }

    @Test
    void deleteReview_WrongId() {

        reviewStorage.deleteReview(999L);

        List<Review> reviews = reviewStorage.findAllReviews(10);

        Assertions.assertThat(reviews)
                .hasSize(2);
    }

    @Test
    void deleteAllReviewByUserId_Normal() {
        reviewStorage.deleteAllReviewByUserId(1L);
        List<Review> reviews = reviewStorage.findAllReviews(10);

        Assertions.assertThat(reviews)
                .hasSize(1);
    }

    @Test
    void deleteAllReviewByUserId_WrongId() {
        reviewStorage.deleteAllReviewByUserId(999L);
        List<Review> reviews = reviewStorage.findAllReviews(10);

        Assertions.assertThat(reviews)
                .hasSize(2);
    }
}