package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
    public Director getDirectorById(Integer directorId) {
        String statement = "select * from directors where director_id = ?";
        return jdbcTemplate.queryForObject(statement, this::makeDirector, directorId);
    }

    @Override
    public List<Director> getDirectorsById(Long filmId) {
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

    public boolean isAllDirectorsExists(List<Integer> directorIds, Integer expectedIdsCount) {
        String baseStatement = "SELECT COUNT (director_name) FROM directors WHERE id IN (";
        StringBuilder queryBuilder = new StringBuilder(baseStatement);
        for (int i = 0; i < directorIds.size(); i++) {
            if (i == directorIds.size() - 1) {
                queryBuilder.append(directorIds.get(i) + " )");
            } else {
                queryBuilder.append(directorIds.get(i) + ", ");
            }
        }
        Integer dbCount = jdbcTemplate.queryForObject(queryBuilder.toString(), Integer.TYPE);
        return dbCount == expectedIdsCount;
    }
}
