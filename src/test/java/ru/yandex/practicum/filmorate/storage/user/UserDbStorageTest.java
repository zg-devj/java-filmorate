package ru.yandex.practicum.filmorate.storage.user;

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
import java.util.Optional;

@SpringBootTest
class UserDbStorageTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private UserStorage userStorage;
    private FilmStorage filmStorage;
    private MpaStorage mpaStorage;
    private FilmGenreStorage filmGenreStorage;
    private GenreStorage genreStorage;
    private DirectorStorage directorStorage;
    private FilmDirectorDbStorage filmDirectorStorage;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(embeddedDatabase);
        filmGenreStorage = new FilmGenreDbStorage(jdbcTemplate, namedParameterJdbcTemplate);
        genreStorage = new GenreDbStorage(jdbcTemplate);
        mpaStorage = new MpaDbStorage(jdbcTemplate);
        directorStorage = new DirectorDbStorage(jdbcTemplate);
        filmDirectorStorage = new FilmDirectorDbStorage(jdbcTemplate, namedParameterJdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, mpaStorage, filmGenreStorage, genreStorage, directorStorage, filmDirectorStorage);
        userStorage = new UserDbStorage(jdbcTemplate, filmStorage);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void findAllUsers_Normal() {
        Collection<User> users = userStorage.findAllUsers();

        Assertions.assertThat(users)
                .hasSize(4);
    }

    @Test
    void findBothUserFriends_Normal() {
        Collection<User> users = userStorage.findBothUserFriends(1L, 2L);

        Assertions.assertThat(users)
                .hasSize(1);
    }

    @Test
    void findBothUserFriends_IsEmpty() {
        Collection<User> users = userStorage.findBothUserFriends(1L, 3L);

        Assertions.assertThat(users)
                .isEmpty();
    }

    @Test
    void findUserById_Normal() {
        Optional<User> userOptional = userStorage.findUserById(1L);

        Assertions.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        Assertions.assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void findUserById_WrongId() {
        Optional<User> userOptional = userStorage.findUserById(999L);

        Assertions.assertThat(userOptional)
                .isNotPresent()
                .isEmpty();
    }

    @Test
    void createUser_Normal() {
        User newUser = User.builder()
                .login("newlogin")
                .name("newuser")
                .email("newuser@example.com")
                .birthday(LocalDate.of(2000, 10, 12))
                .build();
        Long id = userStorage.createUser(newUser).getId();

        Optional<User> userOptional = userStorage.findUserById(id);

        Assertions.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        Assertions.assertThat(user)
                                .hasFieldOrPropertyWithValue("id", 5L)
                                .hasFieldOrPropertyWithValue("email", "newuser@example.com")
                );
    }

    @Test
    void updateUser_Normal() {
        User user1 = User.builder()
                .id(4L)
                .login("login4updated")
                .name("user4updated")
                .email("user4updated@example.com")
                .birthday(LocalDate.of(2000, 10, 12))
                .build();
        userStorage.updateUser(user1);

        Optional<User> userOptional = userStorage.findUserById(user1.getId());

        Assertions.assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        Assertions.assertThat(user)
                                .hasFieldOrPropertyWithValue("id", 4L)
                                .hasFieldOrPropertyWithValue("email", "user4updated@example.com")
                                .hasFieldOrPropertyWithValue("login", "login4updated")
                );
    }

    @Test
    void updateUser_WithWrongId() {
        User user1 = User.builder()
                .id(999L)
                .login("login")
                .name("name")
                .email("login@example.com")
                .birthday(LocalDate.of(2000, 10, 12))
                .build();

        Throwable thrown = Assertions.catchException(() -> userStorage.updateUser(user1));
        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Пользователя с id=%d не существует.", user1.getId()));
    }

    @Test
    void addFriend_Normal() {
        userStorage.addFriend(1L, 4L);

        Collection<User> friends = userStorage.findFriends(1L);

        Assertions.assertThat(friends)
                .hasSize(3);
    }

    @Test
    void addFriend_WrongFriendId() {
        Long friendId = 999L;
        Throwable thrown = Assertions.catchException(() -> userStorage.addFriend(1L, friendId));
        Assertions.assertThat(thrown)
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Нарушение ссылочной целостности");

    }

    @Test
    void removeFriend_Normal() {
        userStorage.removeFriend(1L, 2L);
        Collection<User> friends = userStorage.findFriends(1L);
        Assertions.assertThat(friends)
                .hasSize(1);
    }

    @Test
    void removeFriend_WrongFriendId() {
        Long friendId = 999L;
        Throwable thrown = Assertions.catchException(() -> userStorage.removeFriend(1L, friendId));
        Assertions.assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Пользователя с id=%d не существует.", friendId));
    }

    @Test
    void findFriends_Normal() {
        Collection<User> users = userStorage.findFriends(1L);

        Optional<User> userOptional = userStorage.findUserById(2L);

        Assertions.assertThat(users)
                .hasSize(2)
                .contains(userOptional.get());
    }

    @Test
    void findFriends_Empty() {
        Collection<User> users = userStorage.findFriends(3L);

        Assertions.assertThat(users)
                .hasSize(0)
                .isEmpty();
    }

    @Test
    void checkUser_Normal() {
        Boolean result = userStorage.checkUser(1L);

        Assertions.assertThat(result).isTrue();
    }

    @Test
    void checkUser_WrongIf() {
        Boolean result = userStorage.checkUser(999L);

        Assertions.assertThat(result).isFalse();
    }

}