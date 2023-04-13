package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.services.DirectorService;

import javax.validation.Valid;
import java.util.Collection;


@RestController
@RequestMapping("/directors")
@RequiredArgsConstructor
@Slf4j
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping
    public Collection<Director> getDirectors() {
        log.info("GET /directors - запрос всех режиссеров.");
        return directorService.getDirectors();
    }

    @GetMapping("/{directorId}")
    public Director getDirector(@PathVariable Integer directorId) {
        log.info("GET /directors/{} - запрос режиссера.", directorId);
        return directorService.getDirectorById(directorId);
    }

    @PostMapping
    public Director createDirector(@Valid @RequestBody Director director) {
        log.info("POST /directors - запрос на добавление нового режиссера.");
        return directorService.createDirector(director);
    }

    @PutMapping
    public Director updateDirector(@Valid @RequestBody Director director) {
        log.info("POST /directors - запрос на обновление режиссера.");
        return directorService.updateDirector(director);
    }

    @DeleteMapping("/{directorId}")
    public void deleteDirector(@PathVariable Integer directorId) {
        log.info("DELETE /directors - запрос на удаление режиссера.");
        directorService.deleteDirector(directorId);
    }
}
