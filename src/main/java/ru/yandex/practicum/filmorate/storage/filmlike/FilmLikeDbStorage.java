package ru.yandex.practicum.filmorate.storage.filmlike;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmLikeDbStorage implements FilmLikeStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public boolean create(Long userId, Long filmId) {
        String sql = "INSERT INTO film_like "
                + "(user_id, film_id) "
                + "VALUES (?,?)";
        try {
            if (jdbcTemplate.update(sql, userId, filmId) == 1) {
                return true;
            }
        } catch (DuplicateKeyException e) {
            return false;
        }
        return false;
    }

    @Override
    public boolean delete(Long userId, Long filmId) {
        String sql = "DELETE FROM film_like "
                + "WHERE user_id=? AND film_id=?";
        return jdbcTemplate.update(sql, userId, filmId) == 1;
    }

    @Override
    public void deleteLikesByUserId(Long userId) {
        String sql = "DELETE FROM film_like "
                + "WHERE user_id=?";
        jdbcTemplate.update(sql, userId);
    }
}
