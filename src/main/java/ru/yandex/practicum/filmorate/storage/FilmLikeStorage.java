package ru.yandex.practicum.filmorate.storage;

public interface FilmLikeStorage {
    boolean create(Long userId, Long filmId);

    boolean delete(Long userId, Long filmId);

    void deleteLikesByUserId(Long userId);
}
