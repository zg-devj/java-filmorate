package ru.yandex.practicum.filmorate.services;


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
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.event.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmdirector.FilmDirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.filmdirector.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeDbStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

@SpringBootTest
class FilmServiceTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private FilmStorage filmStorage;
    private UserStorage userStorage;
    private MpaStorage mpaStorage;
    private FilmGenreStorage filmGenreStorage;
    private GenreStorage genreStorage;
    private FilmLikeStorage filmLikeStorage;
    private DirectorStorage directorStorage;
    private FilmDirectorStorage filmDirectorStorage;
    private EventStorage eventStorage;

    private FilmService filmService;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(embeddedDatabase);
        mpaStorage = new MpaDbStorage(jdbcTemplate);
        filmGenreStorage = new FilmGenreDbStorage(jdbcTemplate, namedParameterJdbcTemplate);
        genreStorage = new GenreDbStorage(jdbcTemplate);
        filmLikeStorage = new FilmLikeDbStorage(jdbcTemplate);
        directorStorage = new DirectorDbStorage(jdbcTemplate);
        filmDirectorStorage = new FilmDirectorDbStorage(jdbcTemplate, namedParameterJdbcTemplate);
        eventStorage = new EventDbStorage(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, mpaStorage, filmGenreStorage, genreStorage, directorStorage, filmDirectorStorage);
        userStorage = new UserDbStorage(jdbcTemplate, filmStorage);

        filmService = new FilmService(filmStorage, userStorage, mpaStorage, filmLikeStorage, filmGenreStorage,
                directorStorage, filmDirectorStorage, eventStorage);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void findAllFilms_Normal() {
        List<Film> films = filmService.findAllFilms();

        Assertions.assertThat(films)
                .hasSize(6);
    }

    @Test
    void findFilmById_Normal() {
        Film film = filmService.findFilmById(1L);

        Assertions.assertThat(film)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Комедия 1");
    }

    @Test
    void findFilmById_WrongId() {
        Throwable thrown = Assertions.catchException(() -> filmService.findFilmById(999L));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Фильма с id=%d не существует.", 999L));
    }

    @Test
    void createFilm_Normal() {
        Optional<Mpa> mpa = mpaStorage.findMpaById(1);
        Film film = Film.builder()
                .name("New Film")
                .description("Description")
                .duration(100)
                .releaseDate(LocalDate.of(2000, 1, 1))
                .mpa(mpa.get())
                .genres(new ArrayList<>())
                .directors(new HashSet<>())
                .build();
        filmService.createFilm(film);
        List<Film> films = filmService.findAllFilms();
        Assertions.assertThat(films)
                .hasSize(7);
    }

    @Test
    void updateFilm_Normal() {
        Film film = filmService.findFilmById(1L);
        film.setName("New Name");

        Film updated = filmService.updateFilm(film);

        Assertions.assertThat(updated)
                .hasFieldOrPropertyWithValue("name", "New Name");
    }

    @Test
    void findPopularFilms_Normal_FistFilmWithId3() {
        List<Film> films = filmService.findPopularFilms(Optional.empty(),
                Optional.empty(), 10);

        Assertions.assertThat(films)
                .hasSize(6)
                .first()
                .hasFieldOrPropertyWithValue("id", 3L);

    }

    @Test
    void getAllFilmsByDirectorSorted_Normal_Return4Films_SortLikes() {
        Collection<Film> films = filmService.getAllFilmsByDirectorSorted(1, "likes");

        Assertions.assertThat(films)
                .hasSize(3)
                .first()
                .hasFieldOrPropertyWithValue("id", 5L);
    }

    @Test
    void getAllFilmsByDirectorSorted_Normal_Return4Films_SortYear() {
        Collection<Film> films = filmService.getAllFilmsByDirectorSorted(1, "year");

        Assertions.assertThat(films)
                .hasSize(3)
                .first()
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void sharedUserMovies_Normal() {
        List<Film> films = filmService.sharedUserMovies(1L, 2L);
        Assertions.assertThat(films)
                .hasSize(1);

        List<Film> films2 = filmService.sharedUserMovies(1L, 3L);
        Assertions.assertThat(films2)
                .hasSize(0);

        List<Film> films3 = filmService.sharedUserMovies(3L, 4L);
        Assertions.assertThat(films3)
                .hasSize(0);
    }

    @Test
    void likeFilm_Normal() {
        filmService.likeFilm(5L, 1L);
        filmService.likeFilm(5L, 2L);

        Collection<Film> films = filmService.findPopularFilms(Optional.empty(),
                Optional.empty(), 1);

        Assertions.assertThat(films)
                .hasSize(1)
                .filteredOn(film -> film.getRate() == 2)
                .filteredOn(film -> film.getId() == 5L);
    }

    @Test
    void likeFilm_Exception_TwoLikeFromOneUser() {
        filmService.likeFilm(5L, 1L);

        Collection<Film> films = filmService.findPopularFilms(Optional.empty(),
                Optional.empty(), 1);

        Assertions.assertThat(films)
                .hasSize(1)
                .filteredOn(film -> film.getRate() == 1)
                .filteredOn(film -> film.getId() == 5L);

        Throwable thrown = Assertions.catchException(() -> filmService.likeFilm(5L, 1L));

        Assertions.assertThat(thrown)
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Пользователь уже поставил лайк к фильму.");
    }

    @Test
    void dislikeFilm_Normal() {
        filmService.dislikeFilm(1L, 1L);

        Collection<Film> films = filmService.findPopularFilms(Optional.empty(),
                Optional.empty(), 1);

        Assertions.assertThat(films)
                .hasSize(1)
                .filteredOn(film -> film.getRate() == 0)
                .filteredOn(film -> film.getId() == 1L);
    }

    @Test
    void dislikeFilm_Exception_TwoDislike() {
        filmService.dislikeFilm(1L, 1L);

        Throwable thrown = Assertions.catchException(() -> filmService.dislikeFilm(1L, 1L));

        Assertions.assertThat(thrown)
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Пользователь уже отменил лайк к фильму.");
    }

    @Test
    void findPopularFilms_Top10() {
        List<Film> films = filmService.findPopularFilms(Optional.empty(), Optional.empty(), 10);
        Assertions.assertThat(films)
                .hasSize(6)
                .first()
                .hasFieldOrPropertyWithValue("id", 3L);
    }

    @Test
    void findPopularFilms_Top10_GenreId1_ReturnFilmId1() {
        List<Film> films = filmService.findPopularFilms(
                Optional.of(1), Optional.empty(), 10);

        Assertions.assertThat(films)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", 1L);
    }

    @Test
    void findPopularFilms_Top10_Year2002_ReturnFilmId2() {
        List<Film> films = filmService.findPopularFilms(
                Optional.empty(), Optional.of(2002), 10);

        Assertions.assertThat(films)
                .hasSize(2)
                .first()
                .hasFieldOrPropertyWithValue("id", 2L);
    }

    @Test
    void findPopularFilms_Top10_GenreId1_Year2002_ReturnFilmId2() {
        List<Film> films = filmService.findPopularFilms(
                Optional.of(2), Optional.of(2002), 10);

        Assertions.assertThat(films)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("id", 2L);
    }

}