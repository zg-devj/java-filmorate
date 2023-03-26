package ru.yandex.practicum.filmorate.storage.genre;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GenreDbStorageTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private GenreDbStorage genreDbStorage;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        genreDbStorage = new GenreDbStorage(jdbcTemplate);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void  findAllGenres_Normal() {
        Collection<Genre> genres = genreDbStorage.findAllGenres();

        assertThat(genres)
                .hasSize(6);
    }

    @Test
    void findGenreById_Normal() {
        Optional<Genre> genreOptional = genreDbStorage.findGenreById(1);

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void findGenreById_WrongId() {
        Optional<Genre> genreOptional = genreDbStorage.findGenreById(999);

        assertThat(genreOptional)
                .isNotPresent()
                .isEmpty();
    }

    @Test
    void findGenresByFilmId_Normal() {
        Collection<Genre> genres = genreDbStorage.findGenresByFilmId(1L);

        Optional<Genre> genre = genreDbStorage.findGenreById(3);

        assertThat(genres)
                .hasSize(2)
                .contains(genre.get());
    }

    @Test
    void findGenresByFilmId_WrongId() {
        Collection<Genre> genres = genreDbStorage.findGenresByFilmId(999L);

        assertThat(genres)
                .hasSize(0);
    }
}