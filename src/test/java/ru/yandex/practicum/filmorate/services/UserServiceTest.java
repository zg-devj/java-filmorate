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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.impl.*;

import java.time.LocalDate;
import java.util.Collection;

@SpringBootTest
class UserServiceTest {
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
    private ReviewStorage reviewStorage;
    private ReviewUserStorage reviewUserStorage;
    private EventStorage eventStorage;

    private UserService userService;

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
        filmStorage = new FilmDbStorage(jdbcTemplate, mpaStorage,
                filmGenreStorage, genreStorage, directorStorage, filmDirectorStorage);
        userStorage = new UserDbStorage(jdbcTemplate, filmStorage);
        reviewUserStorage = new ReviewUserDbStorage(jdbcTemplate);
        reviewStorage = new ReviewDbStorage(jdbcTemplate, reviewUserStorage);

        userService = new UserService(userStorage, filmLikeStorage, reviewStorage, reviewUserStorage, eventStorage);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void findAllUsers_Normal() {
        Collection<User> users = userService.findAllUsers();

        Assertions.assertThat(users)
                .hasSize(4);
    }

    @Test
    void findUserById_Normal() {
        User user = userService.findUserById(1L);

        Assertions.assertThat(user)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "user1");
    }

    @Test
    void findUserById_WrongId() {
        Throwable thrown = Assertions.catchException(() -> userService.findUserById(999L));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Пользователь с %d не найден.", 999));
    }

    @Test
    void createUser_Normal() {
        User user = User.builder()
                .name("Name")
                .email("name@name.name")
                .login("name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        User created = userService.createUser(user);

        Collection<User> users = userService.findAllUsers();

        Assertions.assertThat(users)
                .hasSize(5)
                .contains(created);
    }

    @Test
    void updateUser_Normal() {
        User user = userService.findUserById(1L);
        user.setName("new name");

        userService.updateUser(user);

        User updated = userService.findUserById(1L);

        Assertions.assertThat(updated)
                .isNotNull()
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "new name");
    }

    @Test
    void updateUser_WrongId() {
        User wrong = User.builder()
                .id(999L)
                .name("Name")
                .email("name@name.name")
                .login("name")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();

        Throwable thrown = Assertions.catchException(() -> userService.updateUser(wrong));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Пользователя с id=%d не существует.", wrong.getId()));
    }

    @Test
    void addFriend_Normal() {
        userService.addFriend(1L, 4L);

        Collection<User> users = userService.findFriends(1L);

        Assertions.assertThat(users)
                .hasSize(3);
    }

    @Test
    void removeFriend_Normal() {
        userService.removeFriend(1L, 3L);

        Collection<User> users = userService.findFriends(1L);

        Assertions.assertThat(users)
                .hasSize(1);
    }

    @Test
    void commonFriend_Normal() {
        Collection<User> commonFriends = userService.commonFriend(1L, 2L);

        User user = userService.findUserById(3L);

        Assertions.assertThat(commonFriends)
                .hasSize(1)
                .contains(user);
    }

    @Test
    void commonFriend_Normal_ZeroFriends() {
        Collection<User> commonFriends = userService.commonFriend(3L, 4L);

        Assertions.assertThat(commonFriends)
                .hasSize(0);
    }

    @Test
    void commonFriend_Wrong_UserId_Or_OtherUserId() {
        Throwable thrown1 = Assertions.catchException(() -> userService.commonFriend(1L, 999L));

        Assertions.assertThat(thrown1)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Пользователь с %d не найден.", 999));

        Throwable thrown2 = Assertions.catchException(() -> userService.commonFriend(999L, 1L));

        Assertions.assertThat(thrown2)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Пользователь с %d не найден.", 999));
    }

    @Test
    void findFriends_Normal() {
        Collection<User> users = userService.findFriends(1L);

        User user2 = userService.findUserById(3L);
        User user3 = userService.findUserById(3L);

        Assertions.assertThat(users)
                .hasSize(2)
                .contains(user2, user3);
    }

    @Test
    void findFriends_WrongId() {
        Throwable thrown = Assertions.catchException(() -> userService.findFriends(999L));

        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Пользователь с %d не найден.", 999));
    }

    @Test
    void findFriends_NullUser() {
        Throwable thrown = Assertions.catchException(() -> userService.findFriends(null));

        Assertions.assertThat(thrown)
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("id пользователя не должно быть null.");
    }
}