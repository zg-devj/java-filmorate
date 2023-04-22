package ru.yandex.practicum.filmorate.storage.reviewuser;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.LikeDislike;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.ReviewUserStorage;
import ru.yandex.practicum.filmorate.storage.impl.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.impl.ReviewUserDbStorage;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class ReviewUserDbStorageTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private ReviewUserStorage reviewUserStorage;
    private ReviewStorage reviewStorage;

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
    void createLike_Normal_Useful3() {
        reviewUserStorage.createLikeDislike(1L, 1L, LikeDislike.LIKE);
        Optional<Review> reviewOptional = reviewStorage.findReviewById(1L);
        Assertions.assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review -> {
                            Assertions.assertThat(review)
                                    .hasFieldOrPropertyWithValue("reviewId", 1L);
                            Assertions.assertThat(review)
                                    .hasFieldOrPropertyWithValue("useful", 3L);
                        }
                );
    }

    @Test
    void createLike_WrongReviewId() {
        Throwable thrown = Assertions.catchException(() -> reviewUserStorage.createLikeDislike(999L,
                1L, LikeDislike.LIKE));

        Assertions.assertThat(thrown)
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Невозможно поставить лайк отзыву.");
    }

    @Test
    void createLike_WrongUserId() {
        Throwable thrown = Assertions.catchException(() -> reviewUserStorage.createLikeDislike(1L,
                999L, LikeDislike.LIKE));

        Assertions.assertThat(thrown)
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Невозможно поставить лайк отзыву.");
    }

    @Test
    void createLike_Exception_DoubleLike() {
        Throwable thrown = Assertions.catchException(() -> reviewUserStorage.createLikeDislike(1L,
                3L, LikeDislike.LIKE));

        Assertions.assertThat(thrown)
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Невозможно поставить лайк отзыву.");
    }

    @Test
    void createDislike_Normal() {
        reviewUserStorage.createLikeDislike(1L, 1L, LikeDislike.DISLIKE);
        Optional<Review> reviewOptional = reviewStorage.findReviewById(1L);
        Assertions.assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review -> {
                            Assertions.assertThat(review)
                                    .hasFieldOrPropertyWithValue("reviewId", 1L);
                            Assertions.assertThat(review)
                                    .hasFieldOrPropertyWithValue("useful", 1L);
                        }
                );
    }

    @Test
    void createDislike_WrongReviewId() {
        Throwable thrown = Assertions.catchException(() -> reviewUserStorage.createLikeDislike(999L,
                1L, LikeDislike.DISLIKE));

        Assertions.assertThat(thrown)
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Невозможно поставить дизлайк отзыву.");
    }

    @Test
    void createDislike_WrongUserId() {
        Throwable thrown = Assertions.catchException(() -> reviewUserStorage.createLikeDislike(1L,
                999L, LikeDislike.DISLIKE));

        Assertions.assertThat(thrown)
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Невозможно поставить дизлайк отзыву.");
    }

    @Test
    void createDislike_Exception_DoubleDislike() {
        Throwable thrown = Assertions.catchException(() -> reviewUserStorage.createLikeDislike(1L,
                3L,LikeDislike.LIKE));

        Assertions.assertThat(thrown)
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Невозможно поставить лайк отзыву.");
    }

    @Test
    void deleteLikeDislike_Normal() {
        reviewUserStorage.delete(1L, 3L);
        Optional<Review> reviewOptional = reviewStorage.findReviewById(1L);
        Assertions.assertThat(reviewOptional)
                .isPresent()
                .hasValueSatisfying(review -> {
                            Assertions.assertThat(review)
                                    .hasFieldOrPropertyWithValue("reviewId", 1L);
                            Assertions.assertThat(review)
                                    .hasFieldOrPropertyWithValue("useful", 1L);
                        }
                );
    }

    @Test
    void deleteLikeDislike_WrongReviewIdAdnUserId() {
        assertDoesNotThrow(() -> reviewUserStorage.delete(999L, 999L));
    }
}