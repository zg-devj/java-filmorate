package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    private UserController userController;
    private User user;

    @BeforeEach
    void setUp() {
        userController = new UserController(new InMemoryUserStorage());
        user = new User(1L, "user@example.com", "userlogin", "user name",
                LocalDate.of(2000, 01, 01));
    }

    @Test
    public void getAllUsers_ReturnEmptyList_GETMethod() {
        assertEquals(0, userController.allUsers().size(),
                "Не верное количество фильмом");
    }

    @Test
    public void getAllUsers_Return1_GETMethod() {
        User user = new User(1L, "user@example.com", "login", "User Name",
                LocalDate.of(2000, 01, 01));
        userController.createUser(user);

        assertEquals(1, userController.allUsers().size(),
                "Не верное количество фильмом");
    }

    @Test
    public void createUser_WithNormalUser() {
        User user = new User(1L, "user@example.com", "login", "User Name",
                LocalDate.of(2000, 01, 01));
        userController.createUser(user);

        assertEquals(1, userController.allUsers().size());
    }

    @Test
    public void updateUser_WithNormalBehavior() {
        User user = new User(1L, "user@example.com", "login", "User Name",
                LocalDate.of(2000, 01, 01));
        userController.createUser(user);

        User updatedUser = new User(1L, "user2@example.com", "newlogin", "New UserName",
                LocalDate.of(2010, 01, 01));
        userController.updateUser(updatedUser);

        assertEquals(1, userController.allUsers().size());
    }

    @Test
    public void updateUser_WithWrongID_ReturnException() {
        assertEquals(0, userController.allUsers().size(),
                "Не верное количество фильмом");
        final String expected = "Пользователя с id=" + user.getId() + " не существует.";

        doTestUpdate(user, expected);

    }

    private void doTestUpdate(User user, String expected) {
        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> userController.updateUser(user)
        );

        assertEquals(expected, ex.getMessage());
    }
}
