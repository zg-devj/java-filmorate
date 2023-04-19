package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.filmlike.FilmLikeStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.reviewuser.ReviewUserStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.ValidateUtil;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserCleanupService {
    private final UserStorage userStorage;
    private final EventStorage eventStorage;
    private final FilmLikeStorage filmLikeStorage;
    private final ReviewStorage reviewStorage;
    private final ReviewUserStorage reviewUserStorage;

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

        //удалить ленту событий пользователя
        //eventStorage.removeEventsByUserId(id);

        //удалить пользователя из лент событий других пользователей
        //eventStorage.removeEventsByUserEntityId(id);

        //удалить пользователя
        userStorage.removeUser(id);
    }

}
