package ru.yandex.practicum.filmorate.storage.director;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class DirectorDbStorageTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private DirectorStorage directorStorage;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        directorStorage = new DirectorDbStorage(jdbcTemplate);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void getDirectors_Normal() {
        Collection<Director> directors = directorStorage.getDirectors();

        Assertions.assertThat(directors)
                .hasSize(2);
    }

    @Test
    void getDirectorById_Normal() {
        Optional<Director> directorOptional = directorStorage.getDirectorById(1);

        Assertions.assertThat(directorOptional)
                .isPresent()
                .hasValueSatisfying(director ->
                        Assertions.assertThat(director).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void getDirectorById_WrongId() {
        Optional<Director> directorOptional = directorStorage.getDirectorById(999);

        Assertions.assertThat(directorOptional)
                .isNotPresent()
                .isEmpty();

    }

    @Test
    void getDirectorsByFilmId_Normal() {
        List<Director> directors = directorStorage.getDirectorsByFilmId(2L);

        Optional<Director> genre = directorStorage.getDirectorById(2);

        Assertions.assertThat(directors)
                .hasSize(2)
                .contains(genre.get());
    }

    @Test
    void getDirectorsByFilmId_WrongId() {
        List<Director> directors = directorStorage.getDirectorsByFilmId(999L);

        Assertions.assertThat(directors)
                .hasSize(0);
    }

    @Test
    void createDirector_Normal() {
        Director director = Director.builder()
                .name("Режисер 3")
                .build();

        directorStorage.createDirector(director);

        List<Director> directors = directorStorage.getDirectors();

        Assertions.assertThat(directors)
                .hasSize(3)
                .contains(director);
    }

    @Test
    void updateDirector_Normal() {
        Director updatedDirector = Director.builder()
                .id(1)
                .name("Режисер с именем")
                .build();
        directorStorage.updateDirector(updatedDirector);
        Optional<Director> directorOptional = directorStorage.getDirectorById(1);
        Assertions.assertThat(directorOptional)
                .isPresent()
                .hasValueSatisfying(director -> {
                            Assertions.assertThat(director)
                                    .hasFieldOrPropertyWithValue("id", 1);
                            Assertions.assertThat(director)
                                    .hasFieldOrPropertyWithValue("name", "Режисер с именем");
                        }
                );

    }

    @Test
    void deleteDirector_Normal() {
        directorStorage.deleteDirector(2);
        Optional<Director> directorOptional = directorStorage.getDirectorById(2);
        Assertions.assertThat(directorOptional)
                .isNotPresent()
                .isEmpty();
    }

    @Test
    void isDirectorExists_Normal() {
        boolean ret1 = directorStorage.isDirectorExists(1);
        Assertions.assertThat(ret1)
                .isTrue();
    }

    @Test
    void isDirectorExists_WrongId() {
        boolean ret2 = directorStorage.isDirectorExists(999);
        Assertions.assertThat(ret2)
                .isFalse();
    }
}
