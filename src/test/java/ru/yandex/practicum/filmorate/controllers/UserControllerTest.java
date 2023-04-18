package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;
import ru.yandex.practicum.filmorate.services.UserCleanupService;
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.event.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.filmdirector.FilmDirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserControllerTest {

    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private FilmDbStorage filmDbStorage;
    private MpaStorage mpaStorage;
    private FilmGenreStorage filmGenreStorage;
    private GenreStorage genreStorage;
    private UserDbStorage userDbStorage;
    private UserService userService;
    private FilmService filmService;
    private FilmLikeDbStorage filmLikeStorage;
    private UserController userController;
    private DirectorDbStorage directorStorage;
    private FilmDirectorDbStorage filmDirectorStorage;
    private EventDbStorage eventStorage;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        filmGenreStorage = new FilmGenreDbStorage(jdbcTemplate);
        filmLikeStorage = new FilmLikeDbStorage(jdbcTemplate);
        genreStorage = new GenreDbStorage(jdbcTemplate);
        directorStorage = new DirectorDbStorage(jdbcTemplate);
        filmDirectorStorage = new FilmDirectorDbStorage(jdbcTemplate);
        mpaStorage = new MpaDbStorage(jdbcTemplate);
        filmDbStorage = new FilmDbStorage(jdbcTemplate, mpaStorage, filmGenreStorage, genreStorage, directorStorage, filmDirectorStorage);
        userDbStorage = new UserDbStorage(jdbcTemplate, filmDbStorage);
        eventStorage = new EventDbStorage(jdbcTemplate);
        userService = new UserService(userDbStorage, eventStorage);
        filmService = new FilmService(filmDbStorage, userDbStorage, mpaStorage, filmLikeStorage, filmGenreStorage, directorStorage, eventStorage);
        userController = new UserController(userService, filmService);
    }

    @Test
    void shouldReturnFilmsCollectionSize1() {
        Collection<Film> films = userController.getRecommendations(1L);
        assertEquals(1, films.size());
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

}
