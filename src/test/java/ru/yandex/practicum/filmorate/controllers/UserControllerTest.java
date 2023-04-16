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
import ru.yandex.practicum.filmorate.services.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import java.util.Collection;
import static org.junit.jupiter.api.Assertions.assertNull;

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
    private UserController userController;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        filmGenreStorage = new FilmGenreDbStorage(jdbcTemplate);
        genreStorage = new GenreDbStorage(jdbcTemplate);
        mpaStorage = new MpaDbStorage(jdbcTemplate);
        filmDbStorage = new FilmDbStorage(jdbcTemplate, mpaStorage, filmGenreStorage, genreStorage);
        userDbStorage = new UserDbStorage(jdbcTemplate, filmDbStorage);
        userService = new UserService(userDbStorage);
        userController = new UserController(userService);
    }

    @Test
    void shouldReturnNullFilmsCollection() {
        Collection<Film> films = userController.getRecommendations(1L);
        assertNull(films);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

}
