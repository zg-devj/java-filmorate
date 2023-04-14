package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Optional;

public interface ReviewStorage {
    // Все отзывы
    Iterable<Review> findAllReviews(int limit);

    // Все отзывы по фильму
    Iterable<Review> findAllReviewsByFilmId(Long filmId, int limit);

    // Отзыв по идкетификатору
    Optional<Review> findReviewById(Long reviewId);

    Boolean checkReview(Long reviewId);

    Review createReview(Review review);

    Review updateReview(Review review);

    void deleteReview(Long reviewId);

    // Удалить все отзывы пользователя
    void deleteAllReviewByUserId(Long userId);
}
