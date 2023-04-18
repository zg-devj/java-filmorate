package ru.yandex.practicum.filmorate.storage.event;

import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.catchException;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class EventDbStorageTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private EventStorage eventStorage;
    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        eventStorage = new EventDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
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
    public void testAddEventWithInvalidUserId() {
        Throwable thrown = Assertions.catchException(() -> eventStorage.addEvent(1L, 2L, EventStorage.TypeName.FRIEND,
                EventStorage.OperationName.ADD));

        Assertions.assertThat(thrown)
                .isInstanceOf(DataIntegrityViolationException.class);
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
