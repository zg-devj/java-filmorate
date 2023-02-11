package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    private UserController userController;
    private User user;

    @BeforeEach
    void setUp() {
        userController = new UserController();
        user = new User(1, "user@example.com", "userlogin", "user name",
                LocalDate.of(2000, 01, 01));
    }

    @Test
    public void getAllUsers_ReturnEmptyList_GETMethod() {
        assertEquals(0, userController.allUsers().size(),
                "Не верное количество фильмом");
    }

    @Test
    public void getAllUsers_Return1_GETMethod() {
        User user = new User(1, "user@example.com", "login", "User Name",
                LocalDate.of(2000, 01, 01));
        userController.createUser(user);

        assertEquals(1, userController.allUsers().size(),
                "Не верное количество фильмом");
    }

    @Test
    public void createUser_WithNormalUser() {
        User user = new User(1, "user@example.com", "login", "User Name",
                LocalDate.of(2000, 01, 01));
        userController.createUser(user);

        assertEquals(1, userController.allUsers().size());
    }

    @Test
    public void updateUser_WithNormalBehavior() {
        User user = new User(1, "user@example.com", "login", "User Name",
                LocalDate.of(2000, 01, 01));
        userController.createUser(user);

        User updatedUser = new User(1, "user2@example.com", "newlogin", "New UserName",
                LocalDate.of(2010, 01, 01));
        userController.updateUser(updatedUser);

        assertEquals(1, userController.allUsers().size());
    }

    @Test
    public void createUser_WithBlankOrNullEmail_ReturnException() {
        user.setEmail(" ");
        final String expected = "Адрес электронной почты не может быть пустым.";

        doTestCreate(user, expected);

        user.setEmail(null);
        doTestCreate(user, expected);
    }

    @Test
    public void createUser_WithWrongEmail_ReturnException() {
        user.setEmail("user@user@ru");
        final String expected = "Не является адресом электронной почты.";

        doTestCreate(user, expected);
    }

    @Test
    public void createUser_WithBlankLogin_ReturnException() {
        user.setLogin(" ");
        final String expected = "Логин не может быть пустым.";

        doTestCreate(user, expected);
    }

    @Test
    public void createUser_IfBirthdayInFuture_ReturnException() {
        LocalDate now = LocalDate.now();
        user.setBirthday(now.plusDays(1));
        final String expected = "День рождения не может быть в будущем.";

        doTestCreate(user, expected);
    }

    @Test
    public void updateUser_IfUserListEmpty_ReturnException() {
        assertEquals(0, userController.allUsers().size(),
                "Не верное количество пользователей");
        final String expected = "Пользователя с id=" + user.getId() + " не существует.";

        doTestUpdate(user, expected);
    }

    @Test
    public void updateUser_WithBlankOrNullEmail_ReturnException() {
        userController.createUser(user);

        user.setEmail(" ");
        final String expected = "Адрес электронной почты не может быть пустым.";
        doTestUpdate(user, expected);

        user.setEmail(null);
        doTestUpdate(user, expected);
    }

    @Test
    public void updateUser_WithWrongEmail_ReturnException() {
        userController.createUser(user);

        user.setEmail("user@user@ru");
        final String expected = "Не является адресом электронной почты.";

        doTestUpdate(user, expected);
    }

    @Test
    public void updateUser_WithBlankLogin_ReturnException() {
        userController.createUser(user);

        user.setLogin(" ");
        final String expected = "Логин не может быть пустым.";

        doTestUpdate(user, expected);
    }

    @Test
    public void updateUser_IfBirthdayInFuture_ReturnException() {
        userController.createUser(user);

        LocalDate now = LocalDate.now();
        user.setBirthday(now.plusDays(1));
        final String expected = "День рождения не может быть в будущем.";

        doTestUpdate(user, expected);
    }

    private void doTestCreate(User user, String expected) {
        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> userController.createUser(user)
        );

        assertEquals(expected, ex.getMessage());
    }

    private void doTestUpdate(User user, String expected) {
        final ValidationException ex = assertThrows(
                ValidationException.class,
                () -> userController.updateUser(user)
        );

        assertEquals(expected, ex.getMessage());
    }
}
