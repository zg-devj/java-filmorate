package ru.yandex.practicum.filmorate.storage.filmdirector;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.dto.FilmDirectorDto;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class FilmDirectorDbStorage implements FilmDirectorStorage {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public Collection<Director> getFilmDirectors(Long filmId) {
        String statement = "select * from directors as d " +
                "left join film_directors as fd " +
                "on d.director_id = fd.director_id " +
                "where fd.film_id = ?";
        return jdbcTemplate.query(statement, (rs, rowNum) -> Director.builder()
                .id(rs.getInt("director_id"))
                .name("director_name").build(), filmId);
    }

    @Override
    public List<FilmDirectorDto> findFilmDirectorAll(List<Long> filmsIds) {
        SqlParameterSource parameterSource = new MapSqlParameterSource("ids", filmsIds);
        String sql = "SELECT fd.*, d.director_name " +
                "FROM film_directors AS fd " +
                "LEFT JOIN directors AS d on fd.director_id = d.director_id " +
                "WHERE fd.film_id IN (:ids)";
        return namedParameterJdbcTemplate.query(sql, parameterSource,
                (rs, rowNum) -> FilmDirectorDto.builder()
                        .filmId(rs.getLong("film_id"))
                        .directorId(rs.getInt("director_id"))
                        .directorName(rs.getString("director_name"))
                        .build());
    }
}
