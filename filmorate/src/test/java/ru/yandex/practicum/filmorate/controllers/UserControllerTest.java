package ru.yandex.practicum.filmorate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserControllerTest {
    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController();
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
}
