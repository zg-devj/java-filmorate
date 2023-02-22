package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.Identifier;

import java.util.Collection;
import java.util.HashMap;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    // фильмы
    private final HashMap<Long, Film> films = new HashMap<>();
    // для возврата идентификатора
    private Identifier identifier = new Identifier();

    /**
     * Вернуть список всех фильмов
     *
     * @return Collection&lt;Film&gt; Коллекция фильмов
     */
    @Override
    public Collection<Film> findAllFilms() {
        return films.values();
    }

    /**
     * Добавить фильм
     *
     * @param film фильм
     * @return Film добавленный фильм
     */
    @Override
    public Film createFilm(Film film) {
        // устанавливаем идентификатор
        film.setId(identifier.next());
        films.put(film.getId(), film);
        log.info("Добавлен фильм с id={}.", film.getId());
        return film;
    }

    /**
     * Обновить фильм
     *
     * @param film фильм
     * @return измененный фильм
     */
    @Override
    public Film updateFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException(String.format("Фильма с id=%d не существует", film.getId()));
        }
        films.put(film.getId(), film);
        log.info("Обновлен фильм с id={}.", film.getId());
        return film;
    }
}
