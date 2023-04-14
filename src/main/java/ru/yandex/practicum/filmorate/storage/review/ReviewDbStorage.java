package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.reviewuser.ReviewUserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;
    private final ReviewUserStorage reviewUserStorage;

    @Override
    public List<Review> findAllReviews(int limit) {
        String sql = "SELECT r.*, COALESCE(sum(ru.like_it),0) AS useful FROM reviews AS r " +
                "LEFT JOIN review_user AS ru on r.review_id = ru.review_id " +
                "GROUP BY r.review_id " +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::makeReview, limit);
    }

    @Override
    public List<Review> findAllReviewsByFilmId(Long filmId, int limit) {
        String sql = "SELECT r.*, COALESCE(sum(ru.like_it),0) AS useful FROM reviews AS r " +
                "LEFT JOIN review_user AS ru on r.review_id = ru.review_id " +
                "WHERE r.film_id=? " +
                "GROUP BY r.review_id " +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::makeReview, filmId, limit);
    }

    @Override
    public Optional<Review> findReviewById(Long reviewId) {
        String sql = "SELECT r.*, COALESCE(sum(ru.like_it),0) AS useful FROM reviews AS r " +
                "LEFT JOIN review_user AS ru on r.review_id = ru.review_id " +
                "WHERE r.review_id=? " +
                "GROUP BY r.review_id";
        try {
            Review review = jdbcTemplate.queryForObject(sql, this::makeReview, reviewId);
            if (review != null) {
                return Optional.of(review);
            } else {
                return Optional.empty();
            }
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Boolean checkReview(Long reviewId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM reviews WHERE review_id=?)";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getBoolean(1), reviewId);
    }

    @Override
    public Review createReview(Review review) {
        String sql = "INSERT INTO reviews (content, is_positive, user_id, film_id) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setLong(3, review.getUserId());
            ps.setLong(4, review.getFilmId());
            return ps;
        }, keyHolder);
        Long key = keyHolder.getKey().longValue();
        review.setReviewId(key);
        log.debug("Добавлен отзыв с id={}", key);
        return review;
    }

    @Override
    public Review updateReview(Review review) {
        String sql = "UPDATE reviews SET content=?, is_positive=? " +
                "WHERE review_id=?";
        int res = jdbcTemplate.update(sql, review.getContent(), review.getIsPositive(),
                review.getReviewId());
        if (res != 1) {
            throw new NotFoundException(String.format("Отзыва с id=%d не существует.", review.getReviewId()));
        } else {
            review = findReviewById(review.getReviewId()).get();
        }
        return review;
    }

    @Override
    public void deleteReview(Long reviewId) {
        reviewUserStorage.deleteAllByReviewId(reviewId);
        String sql = "DELETE FROM reviews "
                + "WHERE review_id=?";
        jdbcTemplate.update(sql, reviewId);
    }

    @Override
    public void deleteAllReviewByUserId(Long userId) {
        reviewUserStorage.deleteAllByUserId(userId);
        String sql = "DELETE FROM reviews "
                + "WHERE user_id=?";
        jdbcTemplate.update(sql, userId);
    }

    private Review makeReview(ResultSet rs, int rowNum) throws SQLException {
        return Review.builder()
                .reviewId(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .useful(rs.getLong("useful"))
                .build();
    }
}
