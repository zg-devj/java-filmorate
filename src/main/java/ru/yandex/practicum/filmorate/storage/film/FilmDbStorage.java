package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.filmdirector.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.Date;
import java.sql.*;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final MpaStorage mpaStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final GenreStorage genreStorage;
    private final DirectorStorage directorStorage;
    private final FilmDirectorStorage filmDirectorStorage;

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
                        .directors(new HashSet<>())
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
                if (rs.getString("director_name") != null) {
                    int index = list.indexOf(film);
                    list.get(index).getDirectors().add(new Director(rs.getInt("director_id"),
                            rs.getString("director_name")));
                }
            }
            return list;
        };
    }

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
                "g2.genre_name, d.director_id, d.director_name, COALESCE(s.count_like, 0) AS rate " +
                "FROM films as f " +
                "LEFT JOIN mpas AS m on f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_genre AS fg on f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g2 on g2.genre_id = fg.genre_id " +
                "left join film_directors as fd on f.film_id = fd.film_id " +
                "left join directors as d on fd.director_id = d.director_id " +
                "LEFT JOIN (SELECT fl.film_id, COUNT(fl.user_id) AS count_like " +
                "FROM film_like AS fl " +
                "GROUP BY fl.film_id " +
                "LIMIT ?) AS s ON f.film_id = s.film_id " +
                "ORDER BY rate DESC LIMIT ?";
        return jdbcTemplate.query(sql, getListResultSetExtractor(), limit, limit);
    }

    @Override
    public Optional<Film> findFilmById(Long filmId) {
        String sql = "SELECT f.*, m.mpa_name, g2.genre_id," +
                " g2.genre_name, d.director_id, d.director_name, COALESCE(s.count_like, 0) AS rate " +
                "FROM films as f " +
                "LEFT JOIN mpas AS m on f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_genre AS fg on f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g2 on g2.genre_id = fg.genre_id " +
                "left join film_directors as fd on f.film_id = fd.film_id " +
                "left join directors as d on fd.director_id = d.director_id " +
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
        addDirector(film);
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
            addDirector(film);
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

    @Override
    public List<Film> sharedUserMovies(Long userId, Long friendId) {   //получение общих фильмов пользователей
        String sql = "SELECT f.*, m.mpa_name, g2.genre_id, g2.genre_name, COALESCE(s.count_like, 0) AS rate, " +
                "d.director_id, d.director_name " +
                "FROM films AS f " +
                "JOIN film_like AS fl1 ON f.film_id = fl1.film_id AND fl1.user_id = ? " +
                "JOIN film_like AS fl2 ON f.film_id = fl2.film_id AND fl2.user_id = ? " +
                "LEFT JOIN mpas AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g2 ON g2.genre_id = fg.genre_id " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "LEFT JOIN (SELECT fl.film_id, COUNT(fl.user_id) AS count_like " +
                "           FROM film_like AS fl " +
                "           GROUP BY fl.film_id " +
                "          ) AS s ON f.film_id = s.film_id " +
                "ORDER BY rate DESC";

        return jdbcTemplate.query(sql, getListResultSetExtractor(), userId, friendId);
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
                .directors(directorStorage.getDirectorsById(filmId))
                .build();
    }

    private void addDirector(Film film) {
        Long filmId = film.getId();
        jdbcTemplate.update("DELETE FROM film_directors WHERE film_id = ?", filmId);
        Collection<Director> directorSet = film.getDirectors();
        String addDirectorsQuery = "MERGE INTO film_directors (film_id, director_id) " +
                "VALUES (?,?)";
        jdbcTemplate.batchUpdate(addDirectorsQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                Iterator<Director> directorIterator = directorSet.iterator();
                for (int j = 0; j <= i && directorIterator.hasNext(); j++) {
                    Director director = directorIterator.next();
                    if (j == i) {
                        ps.setInt(2, director.getId());
                    }
                }
            }

            @Override
            public int getBatchSize() {
                return directorSet.size();
            }
        });
    }

    @Override
    public Collection<Film> getAllFilmsSorted(Integer directorId, String sortBy) {

        String statement = null;
        if (sortBy.contentEquals("year")) {
            statement = "SELECT f.*, m.mpa_name, g2.genre_id, g2.genre_name, d.director_id, d.director_name," +
                    " COALESCE(s.count_like, 0) AS rate " +
                    "FROM films AS f " +
                    "LEFT JOIN mpas AS m on m.mpa_id = f.mpa_id " +
                    "LEFT JOIN film_genre AS fg on f.film_id = fg.film_id " +
                    "LEFT JOIN genres AS g2 on g2.genre_id = fg.genre_id " +
                    "left join film_directors as fd on f.film_id = fd.film_id " +
                    "left join directors as d on fd.director_id = d.director_id " +
                    "LEFT JOIN (SELECT fl.film_id, " +
                    "COUNT(fl.user_id) AS count_like " +
                    "FROM film_like AS fl " +
                    "GROUP BY fl.film_id) AS s ON f.film_id=s.film_id " +
                    "where fd.director_id = ? order by release_date";

        } else {
            statement = "SELECT f.*, m.mpa_name, g2.genre_id, g2.genre_name, d.director_id, d.director_name, " +
                    "COALESCE(s.count_like, 0) AS rate " +
                    "FROM films AS f " +
                    "LEFT JOIN mpas AS m on m.mpa_id = f.mpa_id " +
                    "LEFT JOIN film_genre AS fg on f.film_id = fg.film_id " +
                    "LEFT JOIN genres AS g2 on g2.genre_id = fg.genre_id " +
                    "left join film_directors as fd on f.film_id = fd.film_id " +
                    "left join directors as d on fd.director_id = d.director_id " +
                    "LEFT JOIN (SELECT fl.film_id, " +
                    "COUNT(fl.user_id) AS count_like " +
                    "FROM film_like AS fl " +
                    "GROUP BY fl.film_id) AS s ON f.film_id=s.film_id " +
                    "where fd.director_id = ? order by rate";
        }

        Collection<Film> list = jdbcTemplate.query(statement, this::makeFilm, directorId);
        return list;
    }

    @Override
    public List<Film> searchForMoviesByDescription(String query, String by) {
        List<Film> films = new ArrayList<>();

        String sql = "SELECT f.*, m.mpa_name, g2.genre_id, g2.genre_name, COALESCE(s.count_like, 0) AS rate, " +
                "d.director_id, d.director_name " +
                "FROM films AS f " +
                "LEFT JOIN mpas AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_genre AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g2 ON g2.genre_id = fg.genre_id " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "LEFT JOIN (SELECT fl.film_id, COUNT(fl.user_id) AS count_like " +
                "           FROM film_like AS fl " +
                "           GROUP BY fl.film_id " +
                "          ) AS s ON f.film_id = s.film_id ";

        String[] params = by.split(",");
        if (params.length == 2) {
            sql += "WHERE 1=1 AND (LOWER(d.director_name) LIKE LOWER(CONCAT('%', ?, '%')) " +
                    "OR LOWER(f.film_name) LIKE LOWER(CONCAT('%', ?, '%')))" +
                    "ORDER BY rate DESC";
            films = jdbcTemplate.query(sql, getListResultSetExtractor(), query, query);
        } else if (params.length == 1 && params[0].equals("director")) {
            sql += "WHERE LOWER(d.director_name) LIKE LOWER(CONCAT('%', ?, '%')) " +
                    "AND f.film_id IN (" +
                    "  SELECT film_id FROM film_directors " +
                    "  JOIN directors ON film_directors.director_id = directors.director_id " +
                    "  WHERE LOWER(director_name) LIKE LOWER(CONCAT('%', ?, '%'))) " +
                    "ORDER BY rate DESC";
            films = jdbcTemplate.query(sql, getListResultSetExtractor(), query, query);
        } else {
            sql += "WHERE LOWER(f.film_name) LIKE LOWER(CONCAT('%', ?, '%')) " +
                    "ORDER BY rate DESC";
            films = jdbcTemplate.query(sql, getListResultSetExtractor(), query);
        }
        return films;
    }
}
