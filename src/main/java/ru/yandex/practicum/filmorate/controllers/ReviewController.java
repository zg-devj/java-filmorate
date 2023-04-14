package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MessageResponse;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.services.ReviewService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping
    public List<Review> findAllReviews(
            @RequestParam Optional<Long> filmId,
            @RequestParam(defaultValue = "10") int count
    ) {
        log.info("GET /reviews?filmId={}&count={} - запрос всех отзовов.", filmId.orElseGet(() -> Long.valueOf("0")), count);
        return reviewService.findAllReviews(filmId, count);
    }

    @GetMapping("/{id}")
    public Review findReviewById(
            @PathVariable Long id
    ) {
        log.info("GET /reviews/{} - запрос отзыва.", id);
        return reviewService.findReviewById(id);
    }

    @PostMapping
    public Review createReview(@Valid @RequestBody Review review) {
        log.info("POST /reviews - запрос на создание отзыва.");
        return reviewService.createReview(review);
    }

    @PutMapping
    public Review updateReview(@Valid @RequestBody Review review) {
        log.info("PUT /reviews - запрос обновление отзыва.");
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteReview(
            @PathVariable Long id
    ) {
        log.info("DELETE /reviews - запрос на удаление отзыва.");
        reviewService.deleteReview(id);
        return ResponseEntity.ok(new MessageResponse("Отзыв удален"));
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<MessageResponse> likeReview(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        log.info("PUT /reviews/{}/like/{} - запрос лайк отзыву.", id, userId);
        reviewService.likeReview(id, userId);
        return ResponseEntity.ok(new MessageResponse("Добавлен лайк"));
    }

    @PutMapping("/{id}/dislike/{userId}")
    public ResponseEntity<MessageResponse> dislikeReview(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        log.info("PUT /reviews/{}/like/{} - запрос дизлайк отзыву.", id, userId);
        reviewService.dislikeReview(id, userId);
        return ResponseEntity.ok(new MessageResponse("Добавлен дизлайк"));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<MessageResponse> deleteLikeReview(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        log.info("DELETE /reviews/{}/like/{} - запрос на удаление лайка к отзыву.", id, userId);
        reviewService.deleteLikeDislikeReview(id, userId);
        return ResponseEntity.ok(new MessageResponse("Удален лайк"));
    }

    @DeleteMapping("/{id}/dislike/{userId}")
    public ResponseEntity<MessageResponse> DeleteDislikeReview(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        log.info("DELETE /reviews/{}/like/{} - запрос на удаление дизлайка к отзыву.", id, userId);
        reviewService.deleteLikeDislikeReview(id, userId);
        return ResponseEntity.ok(new MessageResponse("Удален дизлайк"));
    }
}
