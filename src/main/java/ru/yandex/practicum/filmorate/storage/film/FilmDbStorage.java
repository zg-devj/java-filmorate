package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.*;
import java.sql.Date;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final GenreStorage genreStorage;

    @Override
    public Collection<Film> findAllFilms() {
        String sql = "SELECT * FROM films";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public Collection<Film> findPopularFilms(int limit) {
        String sql = "SELECT * FROM films ORDER BY rate DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::makeFilm, limit);
    }

    @Override
    public Optional<Film> findFilmById(Long filmId) {
        String sql = "SELECT * FROM films WHERE film_id=?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, this::makeFilm, filmId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO films (film_name, description, release_date, duration, mpa_id, rate) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            ps.setLong(6, film.getRate());
            return ps;
        }, keyHolder);
        Long key = keyHolder.getKey().longValue();
        film.setId(key);
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            filmGenreStorage.create(key, film.getGenres());
            film.getGenres().clear();
            film.setGenres(genreStorage.findGenresByFilmId(key));
        } else {
            film.setGenres(new ArrayList<>());
        }
        log.debug("добавлен фильм с id={}", key);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        String sql = "UPDATE films SET film_name=?, description=?, release_date=?, " +
                "duration=?, mpa_id=? WHERE film_id=?";
        int id = jdbcTemplate.update(sql, film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa().getId(),
                film.getId());
        if (id == 1) {
            int mpa_id = film.getMpa().getId();
            // TODO: 24.03.2023 Переделать
            film.setMpa(mpaStorage.findMpaById(mpa_id).get());
            Long key = film.getId();
            if (film.getGenres() != null && film.getGenres().size() >= 0) {
                filmGenreStorage.deleteGenresByFilmId(key);
                filmGenreStorage.create(key, film.getGenres());
                film.getGenres().clear();
                film.setGenres(genreStorage.findGenresByFilmId(key));
            } else {
                film.setGenres(new ArrayList<>());
            }
            return film;
        } else {
            throw new NotFoundException(String.format("Фильм с id=%d не существует.", film.getId()));
        }
    }

    @Override
    public void increaseFilmRate(Long filmId) {
        String sql = "UPDATE films SET rate=rate+1 WHERE film_id=?";
        jdbcTemplate.update(sql, filmId);
    }

    @Override
    public void decreaseFilmRate(Long filmId) {
        String sql = "UPDATE films SET rate=rate-1 WHERE film_id=?";
        jdbcTemplate.update(sql, filmId);
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Long filmId = rs.getLong("film_id");
        Film film = Film.builder()
                .id(filmId)
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .rate(rs.getLong("rate"))
                .mpa(mpaStorage.findMpaById(rs.getInt("mpa_id")).get())
                .genres(genreStorage.findGenresByFilmId(filmId))
                .build();
        return film;
    }
}
