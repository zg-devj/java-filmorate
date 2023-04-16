package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.ValidateUtil;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    // вернуть всех пользователей
    public Collection<User> findAllUsers() {
        Collection<User> allUsers = userStorage.findAllUsers();
        log.info("Запрошены все пользователи в количестве {}.", allUsers.size());
        return allUsers;
    }

    // вернуть пользователя по id
    public User findUserById(Long id) {
        ValidateUtil.validNumberNotNull(id, "id пользователя не должно быть null.");
        User user = userStorage.findUserById(id).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", id));
                    return null;
                }
        );
        log.info("Запрошен пользователь c id={}.", id);
        return user;
    }

    // добавить пользователя
    public User createUser(User user) {
        user.setName(ifStringIsNullOrEmpty(user.getName(), user.getLogin()));
        User created = userStorage.createUser(user);
        log.info("Пользователь с id={} добавлен.", created.getId());
        return created;
    }

    // обновить пользователя
    public User updateUser(User user) {
        user.setName(ifStringIsNullOrEmpty(user.getName(), user.getLogin()));
        User updated = userStorage.updateUser(user);
        log.info("Пользователь с id={} обновлен.", updated.getId());
        return updated;
    }

    // добавление в друзья
    public void addFriend(Long userId, Long friendId) {
        ValidateUtil.validNumberNotNull(userId, "id пользователя не должно быть null.");
        ValidateUtil.validNumberNotNull(friendId, "id друга пользователя не должно быть null.");

        if (!userStorage.checkUser(userId)) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
        }
        if (!userStorage.checkUser(friendId)) {
            ValidateUtil.throwNotFound(String.format("Друг с %d не найден.", friendId));
        }

        userStorage.addFriend(userId, friendId);
        log.info("Пользователь с id={} добавил друга с id={}.", userId, friendId);
        eventStorage.addEvent(userId, friendId, EventStorage.TypeName.FRIEND, EventStorage.OperationName.ADD);
    }

    // удаление из друзей
    public void removeFriend(Long userId, Long friendId) {
        ValidateUtil.validNumberNotNull(userId, "id пользователя не должно быть null.");
        ValidateUtil.validNumberNotNull(friendId, "id друга пользователя не должно быть null.");

        if (!userStorage.checkUser(userId)) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
        }
        if (!userStorage.checkUser(friendId)) {
            ValidateUtil.throwNotFound(String.format("Друг с %d не найден.", friendId));
        }

        userStorage.removeFriend(userId, friendId);
        log.info("Пользователь с id={} удалил из друзей пользователя с id={}.", userId, friendId);
        eventStorage.addEvent(userId, friendId, EventStorage.TypeName.FRIEND, EventStorage.OperationName.REMOVE);
    }

    // список друзей, общих с другим пользователем.
    public Collection<User> commonFriend(Long userId, Long otherId) {
        ValidateUtil.validNumberNotNull(userId, "id пользователя не должно быть null.");
        ValidateUtil.validNumberNotNull(otherId, "id другого пользователя не должно быть null.");

        if (!userStorage.checkUser(userId)) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
        }
        if (!userStorage.checkUser(otherId)) {
            ValidateUtil.throwNotFound(String.format("Другой пользователь с %d не найден.", otherId));
        }

        Collection<User> commons = userStorage.findBothUserFriends(userId, otherId);
        log.info("У пользователей с id={} и id={}, {} общих друзей.",
                userId, otherId, commons.size());
        return commons;
    }

    // возвращаем список пользователей, являющихся его друзьями.
    public Collection<User> findFriends(Long userId) {
        ValidateUtil.validNumberNotNull(userId, "id пользователя не должно быть null.");
        if (!userStorage.checkUser(userId)) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
        }
        return userStorage.findFriends(userId);
    }

    private String ifStringIsNullOrEmpty(String param, String toParam) {
        if (param == null || param.isBlank()) {
            // Если поле не существует или пустое
            return toParam;
        }
        return param;
    }
}
