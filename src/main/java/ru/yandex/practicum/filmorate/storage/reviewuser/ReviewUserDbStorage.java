package ru.yandex.practicum.filmorate.storage.reviewuser;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewUserDbStorage implements ReviewUserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createLike(Long reviewId, Long userId) {
        String sql = "INSERT INTO review_user "
                + "(review_id, user_id, like_it) "
                + "VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sql, reviewId, userId, 1);
        } catch (DuplicateKeyException e) {
            throw new ValidationException("Невозможно поставить лайк отзыву");
        }
    }

    @Override
    public void createDislike(Long reviewId, Long userId) {
        String sql = "INSERT INTO review_user "
                + "(review_id, user_id, like_it) "
                + "VALUES (?, ?, ?)";
        try {
            jdbcTemplate.update(sql, reviewId, userId, -1);
        } catch (DuplicateKeyException e) {
            throw new ValidationException("Невозможно поставить дизлайк отзыву");
        }
    }

    @Override
    public void delete(Long reviewId, Long userId) {
        String sql = "DELETE FROM review_user "
                + "WHERE review_id=? AND user_id=?";
        jdbcTemplate.update(sql, reviewId, userId);
    }

    @Override
    public void deleteAllByUserId(Long userId) {
        String sql = "DELETE FROM review_user "
                + "WHERE user_id=?";
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public void deleteAllByReviewId(Long reviewId) {
        String sql = "DELETE FROM review_user "
                + "WHERE review_id=?";
        jdbcTemplate.update(sql, reviewId);
    }
}
