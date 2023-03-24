package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MessageResponse;
import ru.yandex.practicum.filmorate.services.FilmService;

import javax.validation.Valid;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> allFilms() {
        log.info("GET /films - запрос всех фильмов.");
        return filmService.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film findFilmById(
            @PathVariable Long id
    ) {
        log.info("GET /films/{} - запрос фильма.", id);
        return filmService.findFilmById(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.info("POST /films - запрос на создание нового фильма.");
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("PUT /films - запрос на обновление фильма.");
        return filmService.updateFilm(film);
    }

    @GetMapping("/popular")
    public Collection<Film> findPopularFilms(@RequestParam(defaultValue = "10") int count) {
        log.info("GET /films/popular - запрос популярных фильмов");
        return filmService.findPopularFilms(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity likeFilm(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        log.info("PUT /films/{}/like/{} - запрос на добавление лайка.", id, userId);
        filmService.likeFilm(id, userId);
        return ResponseEntity.ok(new MessageResponse("Поставлен лайк!"));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity dislikeFilm(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        log.info("PUT /films/{}/like/{} - запрос на удаление лайка.", id, userId);
        filmService.dislikeFilm(id, userId);
        return ResponseEntity.ok(new MessageResponse("Лайк удален!"));
    }
}
