package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.services.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> allFilms() {
        Collection<Film> allFilms = filmService.findAllFilms();
        log.debug("Запрошены все фильмы в количестве {}.", allFilms.size());
        return allFilms;
    }

    @GetMapping("/{id}")
    public Film findFilmById(
            @PathVariable Long id
    ) {
        log.debug("Запрошен фильм по id={}.", id);
        return filmService.findFilmById(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.debug("Создание нового фильма.");
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Обновление фильма с id={}.", film.getId());
        return filmService.updateFilm(film);
    }

    @GetMapping("/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.debug("Запрошены {} популярных фильмов.", count);
        return filmService.findPopularFilms(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity likeFilm(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        filmService.likeFilm(id, userId);
        log.debug("Пользователь с id={} поставил лайк фильму с id={}.", userId, id);
        return ResponseEntity.ok("Поставлен лайк!");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity dislikeFilm(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        filmService.dislikeFilm(id, userId);
        log.debug("Пользователь с id={} отменил лайк фильму с id={}.", userId, id);
        return ResponseEntity.ok("Лайк удален!");
    }
}
