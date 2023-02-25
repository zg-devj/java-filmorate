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
        return filmService.findAllFilms();
    }

    @GetMapping("/{id}")
    public Film findFilmById(
            @PathVariable Long id
    ) {
        return filmService.findFilmById(id);
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        return filmService.createFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    @GetMapping("/popular")
    public List<Film> findPopularFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.findPopularFilms(count);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity likeFilm(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        filmService.likeFilm(id, userId);
        return ResponseEntity.ok("Поставлен лайк!");
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity dislikeFilm(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        filmService.dislikeFilm(id, userId);
        return ResponseEntity.ok("Лайк удален!");
    }
}
