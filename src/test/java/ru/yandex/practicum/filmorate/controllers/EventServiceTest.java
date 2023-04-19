package ru.yandex.practicum.filmorate.services;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@SpringBootTest
class EventServiceTest {

    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private EventStorage eventStorage;
    private UserStorage userStorage;
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
        userStorage = new UserDbStorage(jdbcTemplate);
        eventStorage = new EventDbStorage(jdbcTemplate);
        userService = new UserService(userStorage, eventStorage);
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