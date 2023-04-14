package ru.yandex.practicum.filmorate.storage.filmdirector;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class FilmDirectorDbStorage implements FilmDirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Director> getFilmDirectors(Integer filmId) {
        String statement = "select * from directors as d " +
                "left join film_directors as fd " +
                "on d.director_id = fd.director_id " +
                "where fd.film_id = ?";
        return jdbcTemplate.query(statement, (rs, rowNum) -> Director.builder()
                .id(rs.getInt("director_id"))
                .name("director_name").build());
    }

    @Override
    public void addRecord(Integer directorId, Long filmId) {
        Map<String, Integer> directorFilmMap = Map.of("director_id", directorId, "film_id", Math.toIntExact(filmId));
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate).withTableName("films_directors");
        insert.execute(directorFilmMap);
    }

    @Override
    public void addRecords(List<Director> directors, Long filmId) {
        String statement = "insert into film_directors (director_id, film_id) values (?, ?)";
        BatchPreparedStatementSetter setter = new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, directors.get(i).getId());
                ps.setLong(2, filmId);
            }

            @Override
            public int getBatchSize() {
                return directors.size();
            }
        };
        jdbcTemplate.batchUpdate(statement, setter);
    }

    @Override
    public void deleteRecords(Long filmId) {
        String statement = "delete from film_directors where film_id = ?";
        jdbcTemplate.update(statement, filmId);
    }
}
