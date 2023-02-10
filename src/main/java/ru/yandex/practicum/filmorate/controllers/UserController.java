package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.Identifier;
import ru.yandex.practicum.filmorate.utils.ValidateService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    // пользователи
    private final HashMap<Integer, User> users = new HashMap<>();
    // для возврата идентификатора
    private Identifier identifier = new Identifier();

    /**
     * Вернуть список всех пользователей
     *
     * @return Collection&lt;Film&gt; Коллекция пользователей
     */
    @GetMapping
    public Collection<User> allUsers() {
        return users.values();
    }

    /**
     * Добавить пользователя
     *
     * @param user пользователь
     * @return User добавленный пользователь
     */
    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        // валидация
        validate(user);
        // устанавливаем идентификатор
        user.setId(identifier.next());
        users.put(user.getId(), user);
        log.info("добавлен пользователь с id=" + user.getId());

        return user;
    }

    /**
     * Обновить пользователя
     *
     * @param user пользователь
     */
    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        ValidateService.isEmptyList(users.size(), "Пользователей не существует");
        ValidateService.containsFilm(!users.containsKey(user.getId()),
                "Пользователя с id=" + user.getId() + " не существует");
        validate(user);
        users.put(user.getId(), user);
        log.info("Обновлен пользователь с id={}", user.getId());

        return user;
    }

    private void validate(User user) {
        ValidateService.isEmptyStringField(user.getEmail(),
                "Адрес электронной почты не может быть пустым.");
        ValidateService.isNotEmail(user.getEmail(),
                "Не является адресом электронной почты.");
        ValidateService.isEmptyStringField(user.getLogin(),
                "Логин не может быть пустым.");
        ValidateService.dateLaterThenNow(user.getBirthday(),
                "День рождения не может быть в будущем.");
        user.setName(ValidateService.ifStringIsNullOrEmpty(user.getName(), user.getLogin()));
    }
}
