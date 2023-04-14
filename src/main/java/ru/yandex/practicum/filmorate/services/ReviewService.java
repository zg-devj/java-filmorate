package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.reviewuser.ReviewUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.ValidateUtil;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final ReviewUserStorage reviewUserStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;

    public Iterable<Review> findAllReviews(Optional<Long> filmId, int limit) {
        Iterable<Review> reviews;
        if (filmId.isEmpty()) {
            reviews = reviewStorage.findAllReviews(limit);
            log.info("Возвращаем {} отзывово всех фильмом", limit);
        } else {
            validFilm(filmId.get());
            reviews = reviewStorage.findAllReviewsByFilmId(filmId.get(), limit);
            log.info("Возвращаем {} отзывово на фильм {}", limit, filmId.get());
        }
        return reviews;
    }

    public Review findReviewById(Long id) {
        ValidateUtil.validNumberNotNull(id, "id отзыва не должно быть null.");
        Review review = reviewStorage.findReviewById(id).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Отзыв с id=%d не существует.", id));
                    return null;
                }
        );
        log.info("Запрошен отзыв c id={}.", id);
        return review;
    }

    public Review createReview(Review review) {
        validUser(review.getUserId());
        validFilm(review.getFilmId());
        Review created = reviewStorage.createReview(review);
        log.info("Отзыв с id={} добавлен.", created.getReviewId());
        return created;
    }

    public Review updateReview(Review review) {
        validReview(review.getReviewId());
        validUser(review.getUserId());
        validFilm(review.getFilmId());
        Review updated = reviewStorage.updateReview(review);
        log.info("Отзыв с id={} обновлен.", updated.getReviewId());
        return updated;
    }

    public void deleteReview(Long id) {
        ValidateUtil.validNumberNotNull(id, "id отзыва не должно быть null.");
        reviewUserStorage.deleteAllByReviewId(id);
        log.info("удалены все лайки и дизлайки для отзыва с ID={}", id);
        reviewStorage.deleteReview(id);
        log.info("удален отзыв с ID={}", id);
    }

    public void likeReview(Long reviewId, Long userId) {
        validReview(reviewId);
        validUser(userId);
        reviewUserStorage.delete(reviewId, userId);
        reviewUserStorage.createLike(reviewId, userId);
    }

    public void dislikeReview(Long reviewId, Long userId) {
        validReview(reviewId);
        validUser(userId);
        reviewUserStorage.delete(reviewId, userId);
        reviewUserStorage.createDislike(reviewId, userId);
    }

    public void deleteLikeDislikeReview(Long reviewId, Long userId) {
        validReview(reviewId);
        validUser(userId);
        reviewUserStorage.delete(reviewId, userId);
    }

    private void validReview(Long reviewId) {
        ValidateUtil.validNumberNotNull(reviewId, "id отзыва не должно быть null.");
        if (!reviewStorage.chechReview(reviewId)) {
            ValidateUtil.throwNotFound(String.format("Отзыв с %d не найден.", reviewId));
        }
    }

    private void validUser(Long userId) {
        ValidateUtil.validNumberNotNull(userId, "id пользователя не должно быть null.");
        if (!userStorage.checkUser(userId)) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
        }
    }

    private void validFilm(Long filmId) {
        ValidateUtil.validNumberNotNull(filmId, "id фильма не должно быть null.");
        if (!filmStorage.checkFilm(filmId)) {
            ValidateUtil.throwNotFound(String.format("Фильм с %d не найден.", filmId));
        }
    }
}
