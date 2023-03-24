package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.services.MpaService;

import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> findAllMpas() {
        log.info("GET /mpa/ - запрос всех рейтингов.");
        return mpaService.findAllMpas();
    }

    @GetMapping("/{id}")
    public Mpa findMpaById(
            @PathVariable Integer id
    ) {
        log.info("GET /mpa/{} - запрос рейтинга.", id);
        return mpaService.findMpaById(id);
    }
}