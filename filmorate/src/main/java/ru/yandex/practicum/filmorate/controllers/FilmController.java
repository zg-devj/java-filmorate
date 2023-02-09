package ru.yandex.practicum.filmorate.controllers;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.Identifier;

import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
public class FilmController {
    // фильмы
    private final HashMap<Integer, Film> films = new HashMap<>();
    // для возврата идентификатора
    private Identifier identifier = new Identifier();

    /**
     * Вернуть список всех фильмов
     *
     * @return Collection&lt;Film&gt; Коллекция фильмов
     */
    @GetMapping
    public Collection<Film> allFilms() {
        return films.values();
    }

    /**
     * Добавить фильм
     *
     * @param film фильм
     * @return Film добавленный фильм
     */
    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        // устанавливаем идентификатор
        film.setId(identifier.next());
        films.put(film.getId(), film);
        return film;
    }

    /**
     * Обновить фильм
     *
     * @param film фильм
     */
    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            // если объекта нет, то создаем идентификатор для него
            film.setId(identifier.next());
        }
        films.put(film.getId(), film);
        return film;
    }
}
