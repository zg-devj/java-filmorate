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
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.Collection;

@SpringBootTest
class GenreServiceTest {

    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private GenreStorage genreStorage;
    private GenreService genreService;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        genreStorage = new GenreDbStorage(jdbcTemplate);
        genreService = new GenreService(genreStorage);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void findAllGenres_Normal() {
        Collection<Genre> genres = genreService.findAllGenres();
        Assertions.assertThat(genres)
                .hasSize(6);
    }

    @Test
    void findGenreById_Normal() {
        Genre genre = genreService.findGenreById(1);
        Assertions.assertThat(genre)
                .hasFieldOrPropertyWithValue("id", 1)
                .hasFieldOrPropertyWithValue("name", "Комедия");
    }

    @Test
    void findGenreById_WrongId() {
        Throwable thrown = Assertions.catchException(() -> genreService.findGenreById(999));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Жанр с %d не найден.", 999));
    }
}