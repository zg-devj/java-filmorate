package ru.yandex.practicum.filmorate.storage.event;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.impl.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class EventDbStorageTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private EventStorage eventStorage;
    private UserStorage userStorage;
    private MpaStorage mpaStorage;
    private FilmGenreStorage filmGenreStorage;
    private GenreStorage genreStorage;
    private DirectorStorage directorStorage;
    private FilmDirectorStorage filmDirectorStorage;
    private FilmStorage filmStorage;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(embeddedDatabase);
        eventStorage = new EventDbStorage(jdbcTemplate);
        mpaStorage = new MpaDbStorage(jdbcTemplate);
        filmGenreStorage = new FilmGenreDbStorage(jdbcTemplate, namedParameterJdbcTemplate);
        genreStorage = new GenreDbStorage(jdbcTemplate);
        directorStorage = new DirectorDbStorage(jdbcTemplate);
        filmDirectorStorage = new FilmDirectorDbStorage(jdbcTemplate, namedParameterJdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, mpaStorage, filmGenreStorage, genreStorage, directorStorage, filmDirectorStorage);
        userStorage = new UserDbStorage(jdbcTemplate, filmStorage);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void testAddEventWithValidUserId() {
        User user = new User();
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.parse("1946-08-20"));
        userStorage.createUser(user);
        eventStorage.addEvent(1L, 2L, EventStorage.TypeName.FRIEND, EventStorage.OperationName.ADD);
        List<Event> events = eventStorage.getEventsByUserId(1L);
        AssertionsForClassTypes.assertThat(events.get(0).getEntityId()).isEqualTo(2);
    }

    @Test
    void testOrderOfReturnedEventFeed() {
        User user = new User();
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.parse("1946-08-20"));
        userStorage.createUser(user);
        eventStorage.addEvent(1L, 3L, EventStorage.TypeName.FRIEND, EventStorage.OperationName.ADD);
        eventStorage.addEvent(1L, 1L, EventStorage.TypeName.LIKE, EventStorage.OperationName.ADD);
        eventStorage.addEvent(1L, 6L, EventStorage.TypeName.REVIEW, EventStorage.OperationName.ADD);
        List<Event> events = eventStorage.getEventsByUserId(1L);
        AssertionsForClassTypes.assertThat(events.get(0).getEntityId()).isEqualTo(3);
        AssertionsForClassTypes.assertThat(events.get(1).getEntityId()).isEqualTo(1);
        AssertionsForClassTypes.assertThat(events.get(2).getEntityId()).isEqualTo(6);
    }

    @Test
    void testRemoveEventsFromDbByUserId() {
        User user = new User();
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.parse("1946-08-20"));
        userStorage.createUser(user);
        eventStorage.addEvent(1L, 3L, EventStorage.TypeName.FRIEND, EventStorage.OperationName.ADD);
        eventStorage.addEvent(1L, 1L, EventStorage.TypeName.LIKE, EventStorage.OperationName.ADD);
        eventStorage.addEvent(1L, 6L, EventStorage.TypeName.REVIEW, EventStorage.OperationName.ADD);
        eventStorage.removeEventsByUserId(1L);
        List<Event> events = eventStorage.getEventsByUserId(1L);
        AssertionsForClassTypes.assertThat(events).isEqualTo(new ArrayList<>());
    }
}
