package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.Identifier;
import ru.yandex.practicum.filmorate.utils.ValidateService;

import javax.validation.Valid;
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
    public Film createFilm(@Valid @RequestBody Film film) {
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
    public Film updateFilm(@Valid @RequestBody Film film) {
        ValidateService.isEmptyList(films.size(), "Фильмов не существует");
        ValidateService.containsFilm(!films.containsKey(film.getId()),
                "фильма с id=" + film.getId() + " не существует");
        validate(film);
        films.put(film.getId(), film);
        log.info("Обновлен фильм с id={}", film.getId());

        return film;
    }

    private void validate(Film film) {
        ValidateService.isEmptyStringField(film.getName(),
                "Название фильма не может быть пустым.");

        int maxSizeDescription = 200;
        ValidateService.checkMaxSizeStringField(film.getDescription(), maxSizeDescription,
                "Длина описания не должна быть больше " + maxSizeDescription + " символов.");

        LocalDate lessThenDate = LocalDate.of(1895, 12, 28);
        ValidateService.dateLessThen(film.getReleaseDate(), lessThenDate,
                String.format("Дата релиза не может быть раньше %s", lessThenDate));

        ValidateService.durationMoreThenZero(film.getDuration(),
                "Продолжительность фильма должна быть положительной");
    }
}
