package ru.yandex.practicum.filmorate.services;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@SpringBootTest
class FilmServiceTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private FilmDbStorage filmDbStorage;
    private UserDbStorage userDbStorage;
    private MpaStorage mpaStorage;
    private FilmGenreStorage filmGenreStorage;
    private GenreStorage genreStorage;
    private FilmLikeDbStorage filmLikeDbStorage;
    private FilmService filmService;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        mpaStorage = new MpaDbStorage(jdbcTemplate);
        filmGenreStorage = new FilmGenreDbStorage(jdbcTemplate);
        userDbStorage = new UserDbStorage(jdbcTemplate);
        genreStorage = new GenreDbStorage(jdbcTemplate);
        filmLikeDbStorage = new FilmLikeDbStorage(jdbcTemplate);
        filmDbStorage = new FilmDbStorage(jdbcTemplate, mpaStorage, filmGenreStorage, genreStorage);
        filmService = new FilmService(filmDbStorage, userDbStorage, mpaStorage, filmLikeDbStorage, filmGenreStorage);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void likeFilm_Normal() {
        filmService.likeFilm(5L, 1L);
        filmService.likeFilm(5L, 2L);

        Collection<Film> films = filmService.findPopularFilms(1);

        assertThat(films)
                .hasSize(1)
                .filteredOn(film -> film.getRate() == 2)
                .filteredOn(film -> film.getId() == 5L);
    }

    @Test
    void likeFilm_Exception_TwoLikeFromOneUser() {
        filmService.likeFilm(5L, 1L);

        Collection<Film> films = filmService.findPopularFilms(1);

        assertThat(films)
                .hasSize(1)
                .filteredOn(film -> film.getRate() == 1)
                .filteredOn(film -> film.getId() == 5L);

        Throwable thrown = catchException(() -> filmService.likeFilm(5L, 1L));

        assertThat(thrown)
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Пользователь уже поставил лайк к фильму.");
    }

    @Test
    void dislikeFilm_Normal() {
        filmService.dislikeFilm(1L, 1L);

        Collection<Film> films = filmService.findPopularFilms(1);

        assertThat(films)
                .hasSize(1)
                .filteredOn(film -> film.getRate() == 0)
                .filteredOn(film -> film.getId() == 1L);
    }

    @Test
    void dislikeFilm_Exception_TwoDislike() {
        filmService.dislikeFilm(1L, 1L);

        Throwable thrown = catchException(() -> filmService.dislikeFilm(1L, 1L));

        assertThat(thrown)
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Пользователь уже отменил лайк к фильму.");
    }
}