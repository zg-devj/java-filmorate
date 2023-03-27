package ru.yandex.practicum.filmorate.storage.film;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@SpringBootApplication
class FilmDbStorageTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private FilmDbStorage filmDbStorage;
    private MpaStorage mpaStorage;
    private FilmGenreStorage filmGenreStorage;
    private GenreStorage genreStorage;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        mpaStorage = new MpaDbStorage(jdbcTemplate);
        filmGenreStorage = new FilmGenreDbStorage(jdbcTemplate);
        genreStorage = new GenreDbStorage(jdbcTemplate);
        filmDbStorage = new FilmDbStorage(jdbcTemplate, mpaStorage, filmGenreStorage, genreStorage);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void findAllFilms_Normal() {
        Collection<Film> films = filmDbStorage.findAllFilms();

        assertThat(films)
                .hasSize(5);
    }

    @Test
    void findPopularFilms_Normal() {
        Collection<Film> films = filmDbStorage.findPopularFilms(10);

        assertThat(films)
                .hasSize(5)
                .first().hasFieldOrPropertyWithValue("id", 3L);
    }

    @Test
    void findFilmById_Normal() {
        Optional<Film> filmOptional = filmDbStorage.findFilmById(1L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void findFilmById_WrongId() {
        Optional<Film> filmOptional = filmDbStorage.findFilmById(999L);

        assertThat(filmOptional)
                .isNotPresent()
                .isEmpty();
    }

    @Test
    void createFilm_Normal() {
        Film filmCreate = Film.builder()
                .name("film")
                .description("description film")
                .releaseDate(LocalDate.of(2000, 05, 11))
                .duration(100)
                .mpa(mpaStorage.findMpaById(1).get())
                .genres(
                        List.of(genreStorage.findGenreById(1).get(),
                                genreStorage.findGenreById(2).get()))
                .build();
        Long id = filmDbStorage.createFilm(filmCreate).getId();

        Optional<Film> filmOptional = filmDbStorage.findFilmById(id);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("id", 6L)
                                .hasFieldOrPropertyWithValue("name", "film")
                );
        assertThat(filmOptional.get().getGenres())
                .hasSize(2);

        assertThat(filmOptional.get().getMpa())
                .hasFieldOrPropertyWithValue("id", 1);
    }

    @Test
    void updateFilm_Normal() {
        Mpa mpa = mpaStorage.findMpaById(2).get();
        Genre genre = genreStorage.findGenreById(2).get();
        Film filmUpdated = filmDbStorage.findFilmById(1L).get();
        filmUpdated.setName("filmname");
        filmUpdated.setMpa(mpa);
        filmUpdated.setGenres(List.of(genre));

        filmDbStorage.updateFilm(filmUpdated);

        Optional<Film> filmOptional = filmDbStorage.findFilmById(filmUpdated.getId());

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("name", "filmname")
                );
        assertThat(filmOptional.get().getGenres())
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", genre.getId());

        assertThat(filmOptional.get().getMpa())
                .hasFieldOrPropertyWithValue("id", mpa.getId());
    }

    @Test
    void updateFilm_WrongId() {
        Film filmUpdated = Film.builder()
                .id(999L)
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100)
                .mpa(mpaStorage.findMpaById(1).get())
                .build();
        Throwable thrown = catchException(() -> filmDbStorage.updateFilm(filmUpdated));

        assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Фильм с id=%d не существует.", filmUpdated.getId()));
    }

    @Test
    void increaseFilmRate_Normal() {
        Optional<Film> data = filmDbStorage.findFilmById(1L);
        assertThat(data)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("rate", 1L)
                );

        filmDbStorage.increaseFilmRate(1L);

        Optional<Film> filmcheck = filmDbStorage.findFilmById(1L);
        assertThat(filmcheck)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("id", 1L)
                                .hasFieldOrPropertyWithValue("rate", 2L)
                );
    }

    @Test
    void decreaseFilmRate_Normal() {
        Optional<Film> data = filmDbStorage.findFilmById(2L);
        assertThat(data)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("id", 2L)
                                .hasFieldOrPropertyWithValue("rate", 2L)
                );

        filmDbStorage.decreaseFilmRate(2L);

        Optional<Film> filmcheck = filmDbStorage.findFilmById(2L);
        assertThat(filmcheck)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("id", 2L)
                                .hasFieldOrPropertyWithValue("rate", 1L)
                );
    }

    @Test
    void checkFilm_Normal() {
        Boolean result = filmDbStorage.checkFilm(1L);

        assertThat(result).isTrue();
    }

    @Test
    void checkFilm_WrongIf() {
        Boolean result = filmDbStorage.checkFilm(999L);

        assertThat(result).isFalse();
    }
}