package ru.yandex.practicum.filmorate.storage.film;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.impl.*;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class FilmDbStorageTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private FilmStorage filmDbStorage;
    private MpaStorage mpaStorage;
    private FilmGenreStorage filmGenreStorage;
    private GenreStorage genreStorage;
    private DirectorStorage directorStorage;
    private FilmDirectorStorage filmDirectorStorage;


    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder().addScript("schema.sql").addScript("test-data.sql").setType(EmbeddedDatabaseType.H2).build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(embeddedDatabase);
        mpaStorage = new MpaDbStorage(jdbcTemplate);
        filmGenreStorage = new FilmGenreDbStorage(jdbcTemplate, namedParameterJdbcTemplate);
        genreStorage = new GenreDbStorage(jdbcTemplate);
        directorStorage = new DirectorDbStorage(jdbcTemplate);
        filmDirectorStorage = new FilmDirectorDbStorage(jdbcTemplate, namedParameterJdbcTemplate);
        filmDbStorage = new FilmDbStorage(jdbcTemplate, mpaStorage, filmGenreStorage, genreStorage, directorStorage, filmDirectorStorage);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void findAllFilms_Normal() {
        Collection<Film> films = filmDbStorage.findAllFilms();

        Assertions.assertThat(films).hasSize(6);
    }

    @Test
    void findPopularFilms_Normal() {
        Collection<Film> films = filmDbStorage.findPopularFilms(null, null, 10);

        Assertions.assertThat(films).hasSize(6).first().hasFieldOrPropertyWithValue("id", 3L);
    }

    @Test
    void findFilmById_Normal() {
        Optional<Film> filmOptional = filmDbStorage.findFilmById(1L);

        Assertions.assertThat(filmOptional).isPresent()
                .hasValueSatisfying(film -> Assertions.assertThat(film)
                        .hasFieldOrPropertyWithValue("id", 1L));
    }

    @Test
    void findFilmById_WrongId() {
        Optional<Film> filmOptional = filmDbStorage.findFilmById(999L);

        Assertions.assertThat(filmOptional).isNotPresent().isEmpty();
    }

    @Test
    void createFilm_Normal() {
        Film filmCreate = Film.builder().name("film").description("description film")
                .releaseDate(LocalDate.of(2000, 05, 11)).duration(100).mpa(mpaStorage.findMpaById(1).get()).genres(List.of(genreStorage.findGenreById(1).get(), genreStorage.findGenreById(2).get())).directors(new HashSet<>()).build();
        filmCreate.setId(7L);
        Long id = filmDbStorage.createFilm(filmCreate).getId();

        Optional<Film> filmOptional = filmDbStorage.findFilmById(id);

        Assertions.assertThat(filmOptional).isPresent().hasValueSatisfying(film -> Assertions.assertThat(film).hasFieldOrPropertyWithValue("id", 7L).hasFieldOrPropertyWithValue("name", "film"));
        Assertions.assertThat(filmOptional.get().getGenres()).hasSize(2);

        Assertions.assertThat(filmOptional.get().getMpa()).hasFieldOrPropertyWithValue("id", 1);
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

        Assertions.assertThat(filmOptional).isPresent().hasValueSatisfying(film -> Assertions.assertThat(film).hasFieldOrPropertyWithValue("id", 1L).hasFieldOrPropertyWithValue("name", "filmname"));
        Assertions.assertThat(filmOptional.get().getGenres()).hasSize(1).first().hasFieldOrPropertyWithValue("id", genre.getId());

        Assertions.assertThat(filmOptional.get().getMpa()).hasFieldOrPropertyWithValue("id", mpa.getId());
    }

    @Test
    void updateFilm_WrongId() {
        Film filmUpdated = Film.builder().id(999L).name("name").description("description")
                .releaseDate(LocalDate.of(2000, 1, 1))
                .duration(100).mpa(mpaStorage.findMpaById(1).get()).build();
        Throwable thrown = Assertions.catchException(() -> filmDbStorage.updateFilm(filmUpdated));

        Assertions.assertThat(thrown).isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Фильм с id=%d не существует.", filmUpdated.getId()));
    }

    @Test
    void checkFilm_Normal() {
        Boolean result = filmDbStorage.checkFilm(1L);

        Assertions.assertThat(result).isTrue();
    }

    @Test
    void checkFilm_WrongId() {
        Boolean result = filmDbStorage.checkFilm(999L);

        Assertions.assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFilmsCollectionSize1() {
        Collection<Film> films = filmDbStorage.getRecommendations(1L);
        assertEquals(1, films.size());
    }

    @Test
    void sharedUserMovies_Normal() {
        List<Film> sharedMovies = filmDbStorage.commonUserMovies(1L, 2L);

        Assertions.assertThat(sharedMovies)
                .isNotEmpty()
                .hasSize(1)
                .first().hasFieldOrPropertyWithValue("id", 3L);
    }
}