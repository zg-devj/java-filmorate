package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.Identifier;
import ru.yandex.practicum.filmorate.utils.ValidateService;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
@Slf4j
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
        // валидация
        validate(film);

        // устанавливаем идентификатор
        film.setId(identifier.next());
        films.put(film.getId(), film);
        log.info("добавлен фильм с id=" + film.getId());
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
            validate(film);
            films.put(film.getId(), film);
            log.info("Добавлен при обновлении фильм с id={}", film.getId());
        } else {
            films.put(film.getId(), film);
            log.info("Обновлен фильм с id={}", film.getId());
        }

        return film;
    }

    private void validate(Film film) {
        try {
            ValidateService.isEmptyStringField(film.getName(),
                    "Название фильма не может быть пустым.");
            ValidateService.checkMaxSizeStringField(film.getDescription(), 200,
                    "Длина описания не должна быть больше 200 символов.");
            LocalDate lessThenDate = LocalDate.of(1895, 12, 28);
            ValidateService.dateLessThen(film.getReleaseDate(), lessThenDate,
                    String.format("Дата релиза не может быть раньше %s", lessThenDate));
            ValidateService.durationMoreThenZero(film.getDuration(),
                    "Продолжительность фильма должна быть положительной");
        } catch (ValidationException e) {
            log.warn(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
