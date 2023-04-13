package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    @Override
    public Iterable<Review> findAllReviews(int limit) {
        return null;
    }

    @Override
    public Iterable<Review> findAllReviewsByFilmId(Long filmId, int limit) {
        return null;
    }

    @Override
    public Optional<Review> findReviewById(Long reviewId) {
        return Optional.empty();
    }

    @Override
    public boolean chechReview(Long reviewId) {
        return false;
    }

    @Override
    public Review createReview(Review review) {
        return null;
    }

    @Override
    public Review updateReview(Review review) {
        return null;
    }

    @Override
    public void deleteReview(Long reviewId) {

    }

    @Override
    public void deleteAllReviewByUserId(Long userId) {

    }
}
