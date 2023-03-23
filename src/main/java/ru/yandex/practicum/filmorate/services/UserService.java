package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.ValidateUtil;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;

    // вернуть всех пользователей
    public Collection<User> findAllUsers() {
        Collection<User> allUsers = userStorage.findAllUsers();
        log.debug("Запрошены все пользователи в количестве {}.", allUsers.size());
        return allUsers;
    }

    // вернуть пользователя по id
    public User findUserById(Long id) {
        ValidateUtil.validLongNotNull(id, "id пользователя не должно быть null.");
        User user = userStorage.findUserById(id).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", id));
                    return null;
                }
        );
        log.debug("Запрошен пользователь c id={}.", id);
        return user;
    }

    // добавить пользователя
    public User createUser(User user) {
        user.setName(ifStringIsNullOrEmpty(user.getName(), user.getLogin()));
        return userStorage.createUser(user);
    }

    // обновить пользователя
    public User updateUser(User user) {
        user.setName(ifStringIsNullOrEmpty(user.getName(), user.getLogin()));
        return userStorage.updateUser(user);
    }

    // добавление в друзья
    public void addFriend(Long userId, Long friendId) {
        ValidateUtil.validLongNotNull(userId, "id пользователя не должно быть null.");
        ValidateUtil.validLongNotNull(friendId, "id друга пользователя не должно быть null.");

        userStorage.findUserById(userId).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
                    return null;
                }
        );
        userStorage.findUserById(friendId).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Друг с %d не найден.", friendId));
                    return null;
                }
        );

        userStorage.addFriend(userId, friendId);
        log.debug("Пользователь с id={} добавил друга с id={}.", userId, friendId);
    }

    // удаление из друзей
    public void removeFriend(Long userId, Long friendId) {
        ValidateUtil.validLongNotNull(userId, "id пользователя не должно быть null.");
        ValidateUtil.validLongNotNull(friendId, "id друга пользователя не должно быть null.");

        userStorage.findUserById(userId).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
                    return null;
                }
        );
        userStorage.findUserById(friendId).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Друг с %d не найден.", friendId));
                    return null;
                }
        );

        userStorage.removeFriend(userId, friendId);
        log.debug("Пользователь с id={} удалил из друзей пользователя с id={}.", userId, friendId);
    }

    // список друзей, общих с другим пользователем.
    public Collection<User> commonFriend(Long userId, Long otherId) {
        ValidateUtil.validLongNotNull(userId, "id пользователя не должно быть null.");
        ValidateUtil.validLongNotNull(otherId, "id другого пользователя не должно быть null.");

        userStorage.findUserById(userId).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
                    return null;
                }
        );
        userStorage.findUserById(otherId).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Другой пользователь с %d не найден.", userId));
                    return null;
                }
        );

        Collection<User> commons = userStorage.findBothUserFriends(userId, otherId);
        log.debug("У пользователей с id={} и id={}, {} общих друзей.",
                userId, otherId, commons.size());
        return commons;
    }

    // возвращаем список пользователей, являющихся его друзьями.
    public Collection<User> findFriends(Long userId) {
        ValidateUtil.validLongNotNull(userId, "id пользователя не должно быть null.");
        userStorage.findUserById(userId).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
                    return null;
                }
        );
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
