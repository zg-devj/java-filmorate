package ru.yandex.practicum.filmorate.storage.review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.*;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Iterable<Review> findAllReviews(int limit) {
        String sql = "SELECT r.*, sum(ru.like_it) AS useful FROM reviews AS r " +
                "LEFT JOIN review_user AS ru on r.review_id = ru.review_id " +
                "GROUP BY ru.review_id " +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::makeReview, limit);
    }

    @Override
    public Iterable<Review> findAllReviewsByFilmId(Long filmId, int limit) {
        String sql = "SELECT r.*, sum(ru.like_it) AS useful FROM reviews AS r " +
                "LEFT JOIN review_user AS ru on r.review_id = ru.review_id " +
                "WHERE r.FILM_ID = ? " +
                "GROUP BY ru.review_id " +
                "ORDER BY useful DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, this::makeReview, filmId, limit);
    }

    @Override
    public Optional<Review> findReviewById(Long reviewId) {
        String sql = "SELECT r.*, sum(ru.like_it) AS useful FROM reviews AS r " +
                "LEFT JOIN review_user AS ru on r.review_id = ru.review_id " +
                "WHERE r.review_id=?";
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
    public boolean chechReview(Long reviewId) {
        return false;
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
        return null;
    }

    @Override
    public void deleteReview(Long reviewId) {

    }

    @Override
    public void deleteAllReviewByUserId(Long userId) {

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
