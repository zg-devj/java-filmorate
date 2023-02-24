package ru.yandex.practicum.filmorate.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.ValidateUtil;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        ValidateUtil.validUserNotNull(user, String.format("Пользователь с %d не найден.", userId));
        ValidateUtil.validUserNotNull(friend, String.format("Друг с %d не найден.", friendId));

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        
        log.debug("Добавил к userId={} друга friendId={}.", userId, friendId);
    }

    // удаление из друзей
    public void removeFriend(Long userId, Long friendId) {
        ValidateUtil.validLongNotNull(userId, "id пользователя не должно быть null.");
        ValidateUtil.validLongNotNull(friendId, "id друга пользователя не должно быть null.");

        User user = userStorage.findUserById(userId);
        User friend = userStorage.findUserById(friendId);

        ValidateUtil.validUserNotNull(user, String.format("Пользователь с %d не найден.", userId));
        ValidateUtil.validUserNotNull(friend, String.format("Друг с %d не найден.", friendId));

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        log.debug("У пользователя userId={} удален друга friendId={}.", userId, friendId);
    }

    // список друзей, общих с другим пользователем.
    public List<User> commonFriend(Long userId, Long otherId) {
        ValidateUtil.validLongNotNull(userId, "id пользователя не должно быть null.");
        ValidateUtil.validLongNotNull(otherId, "id другого пользователя не должно быть null.");

        User user = userStorage.findUserById(userId);
        User otherUser = userStorage.findUserById(otherId);

        ValidateUtil.validUserNotNull(user, String.format("Пользователь с %d не найден.", userId));
        ValidateUtil.validUserNotNull(otherUser, String.format("Другой пользователь с %d не найден.", otherId));

        List<User> commons = user.getFriends()
                .stream()
                .filter(otherUser.getFriends()::contains)
                .map(userStorage::findUserById)
                .collect(Collectors.toList());

        log.debug("Запрошен список общих друзей у userId={} и otherId={}.", userId, otherId);
        return commons;
    }

    // возвращаем список пользователей, являющихся его друзьями.
    public List<User> findFriends(Long userId) {
        ValidateUtil.validLongNotNull(userId, "id пользователя не должно быть null.");
        User user = userStorage.findUserById(userId);
        ValidateUtil.validUserNotNull(user, String.format("Пользователь с %d не найден.", userId));
        List<User> collect = user.getFriends()
                .stream()
                .map(userStorage::findUserById)
                .collect(Collectors.toList());
        log.debug("Список друзей пользователя userId={}.", userId);
        return collect;
    }

    private String ifStringIsNullOrEmpty(String param, String toParam) {
        if (param == null || param.isBlank()) {
            // Если поле не существует или пустое
            return toParam;
        }
        return param;
    }
}
