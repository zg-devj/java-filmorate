package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final GenreStorage genreStorage;

    @Override
    public List<Film> findAllFilms() {
        String sql = "SELECT f.*, m.mpa_name, COALESCE(s.count_like, 0) AS rate " +
                "FROM films AS f " +
                "LEFT JOIN mpas AS m on m.mpa_id = f.mpa_id " +
                "LEFT JOIN (SELECT fl.film_id, " +
                "COUNT(fl.user_id) AS count_like " +
                "FROM film_like AS fl " +
                "GROUP BY fl.film_id) AS s ON f.film_id=s.film_id " +
                "ORDER BY rate DESC";
        return jdbcTemplate.query(sql, this::makeFilm);
    }

    @Override
    public List<Film> findPopularFilms(int limit) {
        String sql = "SELECT f.*, m.mpa_name, g2.genre_id, " +
                "g2.genre_name, COALESCE(s.count_like, 0) AS rate " +
                "FROM films as f " +
                "LEFT JOIN mpas AS m on f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_genre AS fg on f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g2 on g2.genre_id = fg.genre_id " +
                "LEFT JOIN (SELECT fl.film_id, COUNT(fl.user_id) AS count_like " +
                "FROM film_like AS fl " +
                "GROUP BY fl.film_id " +
                "LIMIT ?) AS s ON f.film_id = s.film_id " +
                "ORDER BY rate DESC LIMIT ?";
        return jdbcTemplate.query(sql, getListResultSetExtractor(), limit, limit);
    }

    @Override
    public Optional<Film> findFilmById(Long filmId) {
        String sql = "SELECT f.*, m.mpa_name, g2.genre_id, g2.genre_name, COALESCE(s.count_like, 0) AS rate " +
                "FROM films as f " +
                "LEFT JOIN mpas AS m on f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_genre AS fg on f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g2 on g2.genre_id = fg.genre_id " +
                "LEFT JOIN (SELECT fl.film_id, COUNT(fl.user_id) AS count_like " +
                "FROM film_like AS fl " +
                "WHERE fl.film_id=? " +
                "GROUP BY fl.film_id) AS s ON f.film_id = s.film_id " +
                "WHERE f.film_id = ?";
        try {
            List<Film> films = jdbcTemplate.query(sql, getListResultSetExtractor(), filmId, filmId);
            if (films != null && films.isEmpty()) {
                return Optional.empty();
            } else {
                return Optional.of(films.get(0));
            }
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }


    @Override
    public Film createFilm(Film film) {
        String sql = "INSERT INTO films (film_name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        Long key = keyHolder.getKey().longValue();
        film.setId(key);
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            filmGenreStorage.create(key, film.getGenres());
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
            int mpaId = film.getMpa().getId();
            film.setMpa(mpaStorage.findMpaById(mpaId).get());
            Long key = film.getId();
            if (film.getGenres() != null) {
                filmGenreStorage.deleteGenresByFilmId(key);
                filmGenreStorage.create(key, film.getGenres());
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
    public Boolean checkFilm(Long filmId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM films WHERE film_id=?)";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getBoolean(1), filmId);
    }

    private static ResultSetExtractor<List<Film>> getListResultSetExtractor() {
        return rs -> {
            List<Film> list = new ArrayList<>();
            while (rs.next()) {
                Film film = Film.builder()
                        .id(rs.getLong("film_id"))
                        .name(rs.getString("film_name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .duration(rs.getInt("duration"))
                        .rate(rs.getInt("rate"))
                        .mpa(new Mpa(rs.getInt("mpa_id"),
                                rs.getString("mpa_name")))
                        .genres(new ArrayList<>())
                        .build();
                if (!list.contains(film)) {
                    list.add(film);
                }
                if (rs.getString("genre_name") != null) {
                    int index = list.indexOf(film);
                    list.get(index).getGenres()
                            .add(new Genre(rs.getInt("genre_id"),
                                    rs.getString("genre_name")));
                }
            }
            return list;
        };
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {
        Long filmId = rs.getLong("film_id");
        return Film.builder()
                .id(filmId)
                .name(rs.getString("film_name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .rate(rs.getInt("rate"))
                .duration(rs.getInt("duration"))
                .mpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")))
                .genres(genreStorage.findGenresByFilmId(filmId))
                .build();
    }

    public Film makeClientFilm(ResultSet rs, int rowNum) throws SQLException {
        return this.makeFilm(rs,rowNum);
    }

}
