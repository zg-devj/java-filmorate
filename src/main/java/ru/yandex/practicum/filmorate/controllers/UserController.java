package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
        return userService.findAllUsers();
    }

    @GetMapping("/{id}")
    public User findUserById(
            @PathVariable Long id
    ) {
        return userService.findUserById(id);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    // добавление в друзья
    @PutMapping("/{id}/friends/{friendId}")
    public ResponseEntity addFriend(
            @PathVariable Long id,
            @PathVariable Long friendId

    ) {
        userService.addFriend(id, friendId);
        return ResponseEntity.ok(new MessageResponse("Друг добавлен"));
    }

    // удаление из друзей
    @DeleteMapping("/{id}/friends/{friendId}")
    public ResponseEntity removeFriend(
            @PathVariable Long id,
            @PathVariable Long friendId

    ) {
        userService.removeFriend(id, friendId);
        return ResponseEntity.ok(new MessageResponse("Друг удален"));
    }

    // список друзей, общих с другим пользователем.
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> findCommonFriend(
            @PathVariable Long id,
            @PathVariable Long otherId
    ) {
        return userService.commonFriend(id, otherId);
    }

    // возвращаем список пользователей, являющихся его друзьями.
    @GetMapping("/{id}/friends")
    public List<User> findFriends(@PathVariable Long id) {
        return userService.findFriends(id);
    }
}
