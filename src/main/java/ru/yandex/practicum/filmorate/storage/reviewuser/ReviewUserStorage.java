package ru.yandex.practicum.filmorate.storage.reviewuser;

import ru.yandex.practicum.filmorate.model.LikeDislike;

public interface ReviewUserStorage {
    // Пользователь ставит лайк отзыву
    // Ползователь ставит дизлайк отзыву
    void createLikeDislike(Long reviewId, Long userId, LikeDislike likeDislike);


    // удаление лайка/дизлайка
    void delete(Long reviewId, Long userId);

    // удаление всех лайков/дизлайков пользователя
    void deleteAllByUserId(Long userId);

    // удаление всех лайков/дизлайков отзыва
    void deleteAllByReviewId(Long reviewId);
}
