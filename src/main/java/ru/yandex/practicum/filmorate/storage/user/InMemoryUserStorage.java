package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.Identifier;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    // пользователи
    private final HashMap<Integer, User> users = new HashMap<>();
    // для возврата идентификатора
    private Identifier identifier = new Identifier();

    /**
     * Вернуть список всех пользователей
     *
     * @return Collection&lt;Film&gt; Коллекция пользователей
     */
    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    /**
     * Добавить пользователя
     *
     * @param user пользователь
     * @return User добавленный пользователь
     */
    @Override
    public User createUser(User user) {
        // устанавливаем идентификатор
        user.setName(ifStringIsNullOrEmpty(user.getName(),user.getLogin()));
        user.setId(identifier.next());
        users.put(user.getId(), user);
        log.info("добавлен пользователь с id=" + user.getId());
        return user;
    }

    /**
     * Обновить пользователя
     *
     * @param user Пользователь
     * @return User Обновленный пользователь
     */
    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new ValidationException(String.format("Пользователя с id=%d не существует.", user.getId()));
        }
        user.setName(ifStringIsNullOrEmpty(user.getName(),user.getLogin()));
        users.put(user.getId(), user);
        log.info("Обновлен пользователь с id={}", user.getId());
        return user;
    }

    public static String ifStringIsNullOrEmpty(String param, String toParam) {
        if (param == null || param.isBlank()) {
            // Если поле не существует или пустое
            return toParam;
        }
        return param;
    }
}
