package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MessageResponse;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("Запрос всех пользователей.");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User findUserById(
            @PathVariable Long id
    ) {
        log.debug("Запрос пользователя.");
        return userService.findUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.debug("Запрос на создание пользователя.");
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("Запрос на обновление пользователя.");
        return userService.updateUser(user);
    }

    // добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity addFriend(
            @PathVariable Long id,
            @PathVariable Long friendId

    ) {
        log.info("Запрос на добавление друга.");
        userService.addFriend(id, friendId);
        return ResponseEntity.ok(new MessageResponse("Друг добавлен."));
    }

    // удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity removeFriend(
            @PathVariable Long id,
            @PathVariable Long friendId

    ) {
        log.info("Запрос на удаление друга.");
        userService.removeFriend(id, friendId);
        return ResponseEntity.ok(new MessageResponse("Друг удален."));
    }

    // список друзей, общих с другим пользователем.
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriend(
            @PathVariable Long id,
            @PathVariable Long otherId
    ) {
        log.info("Запрос общих друзей пользователей.");
        return userService.commonFriend(id, otherId);
    }

    // возвращаем список пользователей, являющихся его друзьями.
    @GetMapping("/{id}/friends")
    public Collection<User> findFriends(@PathVariable Long id) {
        log.info("Запрос друзей пользователя.");
        return userService.findFriends(id);
    }
}
