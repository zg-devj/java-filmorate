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
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> findAllUsers() {
        Collection<User> allUsers = userService.findAllUsers();
        log.debug("Запрошены все пользователи в количестве {}.", allUsers.size());
        return allUsers;
    }

    @GetMapping("/{id}")
    public User findUserById(
            @PathVariable Long id
    ) {
        log.debug("Запрошен пользователь по id={}.", id);
        return userService.findUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.debug("Создание нового пользователя.");
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("Обновление пользователя с id={}.", user.getId());
        return userService.updateUser(user);
    }

    // добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity addFriend(
            @PathVariable Long id,
            @PathVariable Long friendId

    ) {
        log.debug("Пользователь с id={} добавил друга с id={}.", id, friendId);
        userService.addFriend(id, friendId);
        return ResponseEntity.ok(new MessageResponse("Друг добавлен."));
    }

    // удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity removeFriend(
            @PathVariable Long id,
            @PathVariable Long friendId

    ) {
        log.debug("Пользователь с id={} удалил из друзей пользователя с id={}.", id, friendId);
        userService.removeFriend(id, friendId);
        return ResponseEntity.ok(new MessageResponse("Друг удален."));
    }

    // список друзей, общих с другим пользователем.
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriend(
            @PathVariable Long id,
            @PathVariable Long otherId
    ) {
        List<User> users = userService.commonFriend(id, otherId);
        log.debug("У пользователей с id={} и id={}, {} общих друзей.",
                id, otherId, users.size());
        return users;
    }

    // возвращаем список пользователей, являющихся его друзьями.
    @GetMapping("/{id}/friends")
    public List<User> findFriends(@PathVariable Long id) {
        List<User> friends = userService.findFriends(id);
        log.debug("У пользователя с id={} : {} друзей.", friends.size());
        return friends;
    }
}
