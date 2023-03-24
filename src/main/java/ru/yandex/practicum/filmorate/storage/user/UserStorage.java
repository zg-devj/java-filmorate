package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {
    Collection<User> findAllUsers();
    Collection<User> findBothUserFriends(Long user1, Long user2);
    Optional<User> findUserById(Long userId);
    User createUser(User user);
    User updateUser(User user);
    void addFriend(Long userId, Long friendId);
    void removeFriend(Long userId, Long friendId);
    Collection<User> findFriends(Long userId);
    Boolean checkUser(Long userId);
}
