package ru.yandex.practicum.filmorate.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.impl.*;

import java.util.List;

@SpringBootTest
class EventServiceTest {

    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private MpaStorage mpaStorage;
    private FilmGenreStorage filmGenreStorage;
    private GenreStorage genreStorage;
    private FilmLikeStorage filmLikeStorage;
    private DirectorStorage directorStorage;
    private FilmDirectorStorage filmDirectorStorage;
    private ReviewStorage reviewStorage;
    private ReviewUserStorage reviewUserStorage;
    private EventStorage eventStorage;

    private UserService userService;
    private EventService eventService;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(embeddedDatabase);
        mpaStorage = new MpaDbStorage(jdbcTemplate);
        filmGenreStorage = new FilmGenreDbStorage(jdbcTemplate, namedParameterJdbcTemplate);
        genreStorage = new GenreDbStorage(jdbcTemplate);
        filmLikeStorage = new FilmLikeDbStorage(jdbcTemplate);
        directorStorage = new DirectorDbStorage(jdbcTemplate);
        filmDirectorStorage = new FilmDirectorDbStorage(jdbcTemplate, namedParameterJdbcTemplate);
        eventStorage = new EventDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, mpaStorage, filmGenreStorage, genreStorage, directorStorage, filmDirectorStorage);
        userStorage = new UserDbStorage(jdbcTemplate, filmStorage);
        reviewUserStorage = new ReviewUserDbStorage(jdbcTemplate);
        reviewStorage = new ReviewDbStorage(jdbcTemplate, reviewUserStorage);

        userService = new UserService(userStorage, filmLikeStorage, reviewStorage, reviewUserStorage, eventStorage);
        eventService = new EventService(eventStorage, userStorage);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void getEventsByUserId_Normal() {
        userService.addFriend(3L, 1L);
        userService.addFriend(3L, 2L);
        List<Event> eventList = eventService.getEventsByUserId(3L);
        Assertions.assertThat(eventList)
                .hasSize(2);
    }

    @Test
    void getEventsByUserId_WrongId() {
        Throwable thrown = Assertions.catchException(() -> eventService.getEventsByUserId(999L));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Пользователь с %d не найден.", 999));
    }
}