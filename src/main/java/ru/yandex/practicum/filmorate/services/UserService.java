package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collection;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    // вернуть всех пользователей
    public Collection<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    // вернуть пользователя по id
    public User findUserById(Long id) {
        User user = userStorage.findUserById(id);
        if (user == null) {
            throw new NotFoundException(String.format("Пользователя с id=%d не существует.", id));
        }
        return user;
    }

    // добавить пользователя
    public User createUser(User user) {
        user.setName(ifStringIsNullOrEmpty(user.getName(),user.getLogin()));
        return userStorage.createUser(user);
    }

    // обновить пользователя
    public User updateUser(User user) {
        user.setName(ifStringIsNullOrEmpty(user.getName(),user.getLogin()));
        return userStorage.updateUser(user);
    }

    // добавление в друзья
    public void addFriend(Long userId, Long friendId) {
        if (userId == null) {
            String message = "id пользователя не должно быть null";
            log.debug(message);
            throw new ValidationException(message);
        }
        if (friendId == null) {
            String message = "id друга пользователя не должно быть null";
            log.debug(message);
            throw new ValidationException(message);
        }

        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        if (user == null) {
            String message = String.format("Пользователь с %d не найден", userId);
            log.debug(message);
            throw new NotFoundException(message);
        }
        if (friend == null) {
            String message = String.format("Друг с %d не найден", friendId);
            log.debug(message);
            throw new NotFoundException(message);
        }

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.updateUser(user);
        userStorage.updateUser(friend);

        log.debug("Добавил к userId={} друга friendId={}  , ", userId, friendId);
    }

    // удаление из друзей
    // общие друзья

    private String ifStringIsNullOrEmpty(String param, String toParam) {
        if (param == null || param.isBlank()) {
            // Если поле не существует или пустое
            return toParam;
        }
        return param;
    }
}
