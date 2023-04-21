package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.reviewuser.ReviewUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.ValidateUtil;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {
    private final UserStorage userStorage;
    private final FilmLikeStorage filmLikeStorage;
    private final ReviewStorage reviewStorage;
    private final ReviewUserStorage reviewUserStorage;
    private final EventStorage eventStorage;

    // вернуть всех пользователей
    public Collection<User> findAllUsers() {
        Collection<User> allUsers = userStorage.findAllUsers();
        log.info("Запрошены все пользователи в количестве {}.", allUsers.size());
        return allUsers;
    }

    // вернуть пользователя по id
    public User findUserById(Long id) {
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
        checkUser(userId);
        checkUser(friendId);
        userStorage.addFriend(userId, friendId);
        log.info("Пользователь с id={} добавил друга с id={}.", userId, friendId);
        eventStorage.addEvent(userId, friendId, EventStorage.TypeName.FRIEND, EventStorage.OperationName.ADD);
    }

    // удаление из друзей
    public void removeFriend(Long userId, Long friendId) {
        checkUser(userId);
        checkUser(friendId);
        userStorage.removeFriend(userId, friendId);
        log.info("Пользователь с id={} удалил из друзей пользователя с id={}.", userId, friendId);
        eventStorage.addEvent(userId, friendId, EventStorage.TypeName.FRIEND, EventStorage.OperationName.REMOVE);
    }

    // список друзей, общих с другим пользователем.
    public Collection<User> commonFriend(Long userId, Long otherId) {
        checkUser(userId);
        checkUser(otherId);
        Collection<User> commons = userStorage.findBothUserFriends(userId, otherId);
        log.info("У пользователей с id={} и id={}, {} общих друзей.",
                userId, otherId, commons.size());
        return commons;
    }

    // возвращаем список пользователей, являющихся его друзьями.
    public Collection<User> findFriends(Long userId) {
        checkUser(userId);
        return userStorage.findFriends(userId);
    }

    private void checkUser(Long userId) {
        ValidateUtil.validNumberNotNull(userId, "id пользователя не должно быть null.");
        if (!userStorage.checkUser(userId)) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", userId));
        }
    }

    private String ifStringIsNullOrEmpty(String param, String toParam) {
        if (param == null || param.isBlank()) {
            // Если поле не существует или пустое
            return toParam;
        }
        return param;
    }

    public void removeUserById(Long id) {
        if (!userStorage.checkUser(id)) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", id));
        }

        //удалить лайки пользователя
        filmLikeStorage.deleteLikesByUserId(id);

        //удалить друзей пользователя
        userStorage.removeFriendsByUserId(id);

        //удалить все реакции на ревью
        reviewUserStorage.deleteAllByUserId(id);

        //удалить ревью пользователя
        reviewStorage.deleteAllReviewByUserId(id);

        //удалить пользователя
        userStorage.removeUser(id);
    }

}
