package ru.yandex.practicum.filmorate.storage.reviewuser;

public interface ReviewUserStorage {
    // Пользователь ставит лайк отзыву
    void createLike(Long reviewId, Long userId);

    // Ползователь ставит дизлайк отзыву
    void createDislike(Long reviewId, Long userId);

    // удаление лайка/дизлайка
    void delete(Long reviewId, Long userId);

    // удаление всех лайков/дизлайков пользователя
    void deleteAllByUserId(Long userId);

    // удаление всех лайков/дизлайков отзыва
    void deleteAllByReviewId(Long reviewId);
}
