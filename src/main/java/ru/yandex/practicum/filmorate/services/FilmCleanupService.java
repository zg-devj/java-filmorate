package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.filmdirector.FilmDirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.utils.ValidateUtil;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmCleanupService {
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final ReviewStorage reviewStorage;
    private final FilmDirectorStorage filmDirectorStorage;
    private final EventStorage eventStorage;

    public void removeFilmById(Long id) {
        if (!filmStorage.checkFilm(id)) {
            ValidateUtil.throwNotFound(String.format("Фильм с id=%d не найден.", id));
        }

        //Удалить жанры фильма
        genreStorage.deleteFilmGenresByFilmId(id);

        //удалить лайки фильма
        filmStorage.deleteLikesByFilmId(id);

        //удалить ревью
        reviewStorage.deleteAllReviewByFilmId(id);

        //удалить информацию о режиссерах
        filmDirectorStorage.deleteRecords(id);

        // TODO: 19.04.2023 DEL
        //удалить события
        //eventStorage.removeEventsByFilmEntityId(id);

        //Удалить фильм
        filmStorage.deleteFilm(id);
    }
}
