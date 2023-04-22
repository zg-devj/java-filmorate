package ru.yandex.practicum.filmorate.storage.mpa;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.impl.MpaDbStorage;

import java.util.Collection;
import java.util.Optional;

@SpringBootTest
class MpaDbStorageTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private MpaStorage mpaStorage;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        mpaStorage = new MpaDbStorage(jdbcTemplate);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void findAllMpas_Normal() {
        Collection<Mpa> users = mpaStorage.findAllMpas();

        Assertions.assertThat(users)
                .hasSize(5);
    }

    @Test
    void findMpaById_Normal() {
        Optional<Mpa> mpaOptional = mpaStorage.findMpaById(1);

        Assertions.assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        Assertions.assertThat(user).hasFieldOrPropertyWithValue("id", 1)
                );
    }

    @Test
    void findMpaById_WrongId() {
        Optional<Mpa> mpaOptional = mpaStorage.findMpaById(999);

        Assertions.assertThat(mpaOptional)
                .isNotPresent()
                .isEmpty();
    }
}