package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> getDirectors() {
        String statement = "select * from directors";
        return jdbcTemplate.query(statement, this::makeDirector);
    }

    @Override
    public Optional<Director> getDirectorById(Integer directorId) {
        String statement = "select * from directors where director_id = ?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(statement, this::makeDirector, directorId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Director> getDirectorsByFilmId(Long filmId) {
        String statement = "select * from directors as d " +
                "left join film_directors as fd " +
                "on d.director_id = fd.director_id " +
                "where fd.film_id = ?";
        return jdbcTemplate.query(statement, this::makeDirector, filmId);
    }

    @Override
    public Director createDirector(Director director) {
        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate).withTableName("directors").usingGeneratedKeyColumns("director_id");
        int directorId = insert.executeAndReturnKey(director.toMap()).intValue();

        director.setId(directorId);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String statement = "update directors set director_name = ? where director_id = ?";
        jdbcTemplate.update(statement, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(Integer directorId) {
        String statement = "delete from directors where director_id = ?";
        jdbcTemplate.update(statement, directorId);
    }

    @Override
    public boolean isDirectorExists(Integer directorId) {
        String statement = "select * from directors where director_id = ?";

        List<Director> userList = jdbcTemplate.query(statement, this::makeDirector, directorId);
        return !userList.isEmpty();
    }

    private Director makeDirector(ResultSet rs, int rowNum) throws SQLException {
        return Director.builder().id(rs.getInt("director_id")).name(rs.getString("director_name")).build();
    }
}
