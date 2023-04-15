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
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import java.util.Collection;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class UserControllerTest {

    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
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
        userDbStorage = new UserDbStorage(jdbcTemplate);
        userService = new UserService(userDbStorage);
        userController = new UserController(userService);
    }

    @Test
    void shouldReturnEmptyFilmsCollection() {
        Collection<Film> films = userController.getRecommendations(1L);
        assertThat(films).isEmpty();
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

}
