package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDbStorage implements  FilmStorage{

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<Film> findAllFilms() {
        return null;
    }

    @Override
    public Film findFilmById(Long id) {
        return null;
    }

    @Override
    public Film createFilm(Film film) {
        return null;
    }

    @Override
    public Film updateFilm(Film film) {
        return null;
    }
}
