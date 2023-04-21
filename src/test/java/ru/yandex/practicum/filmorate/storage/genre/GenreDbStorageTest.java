package ru.yandex.practicum.filmorate.storage.genre;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.impl.GenreDbStorage;

import java.util.Collection;
import java.util.Optional;

@SpringBootTest
class GenreDbStorageTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private GenreStorage genreStorage;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        genreStorage = new GenreDbStorage(jdbcTemplate);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void findAllGenres_Normal() {
        Collection<Genre> genres = genreStorage.findAllGenres();

        Assertions.assertThat(genres)
                .hasSize(6);
    }

    @Test
    void findGenreById_Normal() {
        Optional<Genre> genreOptional = genreStorage.findGenreById(1);

        Assertions.assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        Assertions.assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void findGenreById_WrongId() {
        Optional<Genre> genreOptional = genreStorage.findGenreById(999);

        Assertions.assertThat(genreOptional)
                .isNotPresent()
                .isEmpty();
    }

    @Test
    void findGenresByFilmId_Normal() {
        Collection<Genre> genres = genreStorage.findGenresByFilmId(1L);

        Optional<Genre> genre = genreStorage.findGenreById(3);

        Assertions.assertThat(genres)
                .hasSize(2)
                .contains(genre.get());
    }

    @Test
    void findGenresByFilmId_WrongId() {
        Collection<Genre> genres = genreStorage.findGenresByFilmId(999L);

        Assertions.assertThat(genres)
                .hasSize(0);
    }
}