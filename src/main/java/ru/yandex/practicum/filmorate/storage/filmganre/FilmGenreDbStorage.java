package ru.yandex.practicum.filmorate.storage.filmganre;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void create(Long filmId, List<Genre> genres) {
        String sql = "INSERT INTO film_genre "
                + "(film_id, genre_id) "
                + "VALUES (?, ?)";

        List<Genre> noDublicateList =
                new ArrayList<>(new LinkedHashSet<>(genres));

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = noDublicateList.get(i);
                ps.setLong(1, filmId);
                ps.setInt(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return noDublicateList.size();
            }
        });
    }

    @Override
    public void deleteGenresByFilmId(Long filmId) {
        String sql = "DELETE FROM film_genre "
                + "WHERE film_id=?";
        jdbcTemplate.update(sql, filmId);
    }
}