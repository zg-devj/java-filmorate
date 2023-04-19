package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.MessageResponse;
import ru.yandex.practicum.filmorate.services.FilmCleanupService;
import ru.yandex.practicum.filmorate.services.FilmService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private final FilmCleanupService filmCleanupService;

    @GetMapping
    public List<Film> allFilms() {
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

    @GetMapping("/director/{directorId}")
    public Collection<Film> getDirectorFilms(@PathVariable Integer directorId,
                                             @RequestParam(required = true) String sortBy) {
        log.info(String.format("Пришёл запрос на получение режиссера с id = %d, сортировка по %s", directorId, sortBy));
        return filmService.getAllFilmsByDirectorSorted(directorId, sortBy);
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
    public List<Film> findPopularFilms(
            @RequestParam Optional<Integer> genreId,
            @RequestParam Optional<Integer> year,
            @RequestParam(defaultValue = "10") int count
    ) {
        log.info("GET /films/popular - запрос популярных фильмов");
        return filmService.findPopularFilms(genreId, year, count);
    }

    @PutMapping("/{id}/like/{userId}")
    public ResponseEntity<MessageResponse> likeFilm(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        log.info("PUT /films/{}/like/{} - запрос на добавление лайка.", id, userId);
        filmService.likeFilm(id, userId);
        return ResponseEntity.ok(new MessageResponse("Поставлен лайк!"));
    }

    @DeleteMapping("/{id}/like/{userId}")
    public ResponseEntity<MessageResponse> dislikeFilm(
            @PathVariable Long id,
            @PathVariable Long userId
    ) {
        log.info("PUT /films/{}/like/{} - запрос на удаление лайка.", id, userId);
        filmService.dislikeFilm(id, userId);
        return ResponseEntity.ok(new MessageResponse("Лайк удален!"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<MessageResponse> deleteFilm(@PathVariable Long id) {
        log.info("DELETE /films/{} - запрос на удаление фильма.", id);
        filmCleanupService.removeFilmById(id);
        return ResponseEntity.ok(new MessageResponse("Фильм удален!"));
    }

    @GetMapping("/common")
    public List<Film> sharedUserMovies(
            @RequestParam Long userId, Long friendId
    ) {
        log.info("GET /common - запрос общих фильмов пользователей");
        return filmService.sharedUserMovies(userId, friendId);
    }

    @GetMapping("/search")
    public List<Film> searchForMoviesByDescription(
            @RequestParam String query,
            @RequestParam(name = "by", required = false) String by
    ) {
        log.info("GET /films/search?query=" + query + "&by= " + by);
        return filmService.searchForMoviesByDescription(query, by);
    }
}
