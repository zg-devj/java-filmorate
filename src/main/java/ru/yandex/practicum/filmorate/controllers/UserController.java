package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.MessageResponse;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.services.UserCleanupService;
import ru.yandex.practicum.filmorate.services.UserService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserCleanupService userCleanupService;

    @GetMapping
    public Collection<User> findAllUsers() {
        log.info("GET /user - все пользователи");
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User findUserById(
            @PathVariable Long id
    ) {
        log.info("GET /users/{} - пользователь", id);
        return userService.findUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("POST /users - создание пользователя");
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("PUT /users - обновление пользователя");
        return userService.updateUser(user);
    }

    // удалить пользователя
    @DeleteMapping("/{id}")
    public ResponseEntity removeUser(@PathVariable Long id) {
        log.info("DELETE /users/{} - запрос на удаление пользователя.", id);
        userCleanupService.removeUserById(id);
        return ResponseEntity.ok(new MessageResponse("Пользователь удален."));
    }

    // добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity addFriend(
            @PathVariable Long id,
            @PathVariable Long friendId

    ) {
        log.info("PUT /users/{}/friends/{} - запрос на добавление друга.", id, friendId);
        userService.addFriend(id, friendId);
        return ResponseEntity.ok(new MessageResponse("Друг добавлен."));
    }

    // удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity removeFriend(
            @PathVariable Long id,
            @PathVariable Long friendId

    ) {
        log.info("DELETE /users/{}/friends/{} - запрос на удаление друга.", id, friendId);
        userService.removeFriend(id, friendId);
        return ResponseEntity.ok(new MessageResponse("Друг удален."));
    }

    // список друзей, общих с другим пользователем.
    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<User> findCommonFriend(
            @PathVariable Long id,
            @PathVariable Long otherId
    ) {
        log.info("GET /users/{}/friends/common/{} - запрос общих друзей пользователей.", id, otherId);
        return userService.commonFriend(id, otherId);
    }

    // возвращаем список пользователей, являющихся его друзьями.
    @GetMapping("/{id}/friends")
    public Collection<User> findFriends(@PathVariable Long id) {
        log.info("GET /users/{}/friends - запрос друзей пользователя.", id);
        return userService.findFriends(id);
    }
}
