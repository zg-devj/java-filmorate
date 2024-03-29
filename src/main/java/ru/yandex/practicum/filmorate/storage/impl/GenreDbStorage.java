package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Genre> findAllGenres() {
        String sql = "SELECT * FROM genres";
        return jdbcTemplate.query(sql, this::makeGenre);
    }

    @Override
    public Optional<Genre> findGenreById(Integer genreId) {
        String sql = "SELECT * FROM genres WHERE genre_id=?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, this::makeGenre, genreId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Genre> findGenresByFilmId(Long filmId) {
        String sql = "SELECT fg.genre_id, g.genre_name " +
                "FROM  film_genre AS fg " +
                "JOIN genres AS g ON fg.genre_id=g.genre_id " +
                "WHERE fg.film_id=?";
        return jdbcTemplate.query(sql, this::makeGenre, filmId);
    }

    @Override
    public void deleteFilmGenresByFilmId(Long id) {
        String sql = "DELETE FROM film_genre "
                + "WHERE film_id=?";
        jdbcTemplate.update(sql, id);
    }

    private Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("genre_name"))
                .build();
    }
}
