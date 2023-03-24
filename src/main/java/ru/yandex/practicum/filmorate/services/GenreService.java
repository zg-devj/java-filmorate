package ru.yandex.practicum.filmorate.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.utils.ValidateUtil;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class GenreService {
    private final GenreStorage genreStorage;

    public Collection<Genre> findAllGenres() {
        Collection<Genre> allGenres = genreStorage.findAllGenres();
        log.debug("Запрошены жанры в количестве {}.", allGenres.size());
        return allGenres;
    }

    public Genre findGenreById(Integer genreId) {
        ValidateUtil.validNumberNotNull(genreId, "id жанра не должно быть null.");
        Genre genre = genreStorage.findGenreById(genreId).orElseThrow(
                () -> {
                    ValidateUtil.throwNotFound(String.format(":Жанр с %d не найден.", genreId));
                    return null;
                }
        );
        log.debug("Запрошен жанр c id={}.", genreId);
        return genre;
    }
}
