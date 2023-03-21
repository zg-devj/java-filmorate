package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.Identifier;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    // пользователи
    private final HashMap<Long, User> users = new HashMap<>();
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
     * Веруть пользователя по id
     * @param id идентификатор пользователя
     * @return пользователя
     */
    @Override
    public User findUserById(Long id) {
        return users.get(id);
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
        user.setId(identifier.next());
        users.put(user.getId(), user);
        log.debug("добавлен пользователь с id=" + user.getId());
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
            throw new NotFoundException(String.format("Пользователя с id=%d не существует.",user.getId()));
        }
        users.put(user.getId(), user);
        log.debug("Обновлен пользователь с id={}", user.getId());
        return user;
    }
}
