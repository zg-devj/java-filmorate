package ru.yandex.practicum.filmorate.storage.user;

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
import ru.yandex.practicum.filmorate.storage.director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.filmdirector.FilmDirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreDbStorage;
import ru.yandex.practicum.filmorate.storage.filmganre.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchException;

@SpringBootTest
class UserDbStorageTest {
    private EmbeddedDatabase embeddedDatabase;
    private JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private UserDbStorage userDbStorage;
    private FilmDbStorage filmDbStorage;
    private MpaStorage mpaStorage;
    private FilmGenreStorage filmGenreStorage;
    private GenreStorage genreStorage;
    private DirectorDbStorage directorStorage;
    private FilmDirectorDbStorage filmDirectorStorage;

    @BeforeEach
    void setUp() {
        embeddedDatabase = new EmbeddedDatabaseBuilder()
                .addScript("schema.sql")
                .addScript("test-data.sql")
                .setType(EmbeddedDatabaseType.H2)
                .build();
        jdbcTemplate = new JdbcTemplate(embeddedDatabase);
        filmGenreStorage = new FilmGenreDbStorage(jdbcTemplate, namedParameterJdbcTemplate);
        genreStorage = new GenreDbStorage(jdbcTemplate);
        mpaStorage = new MpaDbStorage(jdbcTemplate);
        directorStorage = new DirectorDbStorage(jdbcTemplate);
        filmDirectorStorage = new FilmDirectorDbStorage(jdbcTemplate);
        filmDbStorage = new FilmDbStorage(jdbcTemplate, mpaStorage, filmGenreStorage, genreStorage, directorStorage, filmDirectorStorage);
        userDbStorage = new UserDbStorage(jdbcTemplate, filmDbStorage);
    }

    @AfterEach
    void tearDown() {
        embeddedDatabase.shutdown();
    }

    @Test
    void findAllUsers_Normal() {
        Collection<User> users = userDbStorage.findAllUsers();

        assertThat(users)
                .hasSize(4);
    }

    @Test
    void findBothUserFriends_Normal() {
        Collection<User> users = userDbStorage.findBothUserFriends(1L, 2L);

        assertThat(users)
                .hasSize(1);
    }

    @Test
    void findBothUserFriends_IsEmpty() {
        Collection<User> users = userDbStorage.findBothUserFriends(1L, 3L);

        assertThat(users)
                .isEmpty();
    }

    @Test
    void findUserById_Normal() {
        Optional<User> userOptional = userDbStorage.findUserById(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void findUserById_WrongId() {
        Optional<User> userOptional = userDbStorage.findUserById(999L);

        assertThat(userOptional)
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
        Long id = userDbStorage.createUser(newUser).getId();

        Optional<User> userOptional = userDbStorage.findUserById(id);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user)
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
        userDbStorage.updateUser(user1);

        Optional<User> userOptional = userDbStorage.findUserById(user1.getId());

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user)
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

        Throwable thrown = catchException(() -> userDbStorage.updateUser(user1));
        assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Пользователя с id=%d не существует.", user1.getId()));
    }

    @Test
    void addFriend_Normal() {
        userDbStorage.addFriend(1L, 4L);

        Collection<User> friends = userDbStorage.findFriends(1L);

        assertThat(friends)
                .hasSize(3);
    }

    @Test
    void addFriend_WrongFriendId() {
        Long friendId = 999L;
        Throwable thrown = catchException(() -> userDbStorage.addFriend(1L, friendId));
        assertThat(thrown)
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Нарушение ссылочной целостности");

    }

    @Test
    void removeFriend_Normal() {
        userDbStorage.removeFriend(1L, 2L);
        Collection<User> friends = userDbStorage.findFriends(1L);
        assertThat(friends)
                .hasSize(1);
    }

    @Test
    void removeFriend_WrongFriendId() {
        Long friendId = 999L;
        Throwable thrown = catchException(() -> userDbStorage.removeFriend(1L, friendId));
        assertThat(thrown)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining(String.format("Пользователя с id=%d не существует.", friendId));
    }

    @Test
    void findFriends_Normal() {
        Collection<User> users = userDbStorage.findFriends(1L);

        Optional<User> userOptional = userDbStorage.findUserById(2L);

        assertThat(users)
                .hasSize(2)
                .contains(userOptional.get());
    }

    @Test
    void findFriends_Empty() {
        Collection<User> users = userDbStorage.findFriends(3L);

        assertThat(users)
                .hasSize(0)
                .isEmpty();
    }

    @Test
    void checkUser_Normal() {
        Boolean result = userDbStorage.checkUser(1L);

        assertThat(result).isTrue();
    }

    @Test
    void checkUser_WrongIf() {
        Boolean result = userDbStorage.checkUser(999L);

        assertThat(result).isFalse();
    }

}