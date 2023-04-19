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
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DirectorServiceTest {

    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private DirectorStorage directorStorage;

    private DirectorService directorService;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        directorStorage = new DirectorDbStorage(jdbcTemplate);
        directorService = new DirectorService(directorStorage);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void getDirectors_Normal() {
        Collection<Director> directors = directorService.getDirectors();
        Assertions.assertThat(directors)
                .hasSize(2);
    }

    @Test
    void getDirectorById_Normal() {
        Director director = directorService.getDirectorById(1);

        Assertions.assertThat(director)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "Режисер 1");
    }

    @Test
    void getDirectorById_WrongId() {
        Throwable thrown = Assertions.catchException(() -> directorService.getDirectorById(999));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Режиссера с id %d нет в базе", 999));
    }

    @Test
    void createDirector_Normal() {
        Director newDirector = Director.builder()
                .name("Режисер 3")
                .build();

        Director created = directorService.createDirector(newDirector);

        Collection<Director> directorList = directorService.getDirectors();

        Assertions.assertThat(directorList)
                .hasSize(3)
                .contains(created);
    }

    @Test
    void updateDirector_Normal() {
        Director director = directorService.getDirectorById(1);
        director.setName("Режисер с именем");

        directorService.updateDirector(director);

        Director directorUpdated = directorService.getDirectorById(1);

        Assertions.assertThat(directorUpdated)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "Режисер с именем");
    }

    @Test
    void updateDirector_WrongId() {
        Director wrong = Director.builder()
                .id(999)
                .name("Режисер")
                .build();
        Throwable thrown = Assertions.catchException(() -> directorService.updateDirector(wrong));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Режиссера с id %d нет в базе", wrong.getId()));
    }

    @Test
    void deleteDirector_Normal() {
        directorService.deleteDirector(1);

        Collection<Director> directors = directorService.getDirectors();

        Assertions.assertThat(directors)
                .hasSize(1);
    }

    @Test
    void deleteDirector_WrongId() {
        Throwable thrown = Assertions.catchException(() -> directorService.deleteDirector(999));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Режиссера с id %d нет в базе", 999));
    }
}