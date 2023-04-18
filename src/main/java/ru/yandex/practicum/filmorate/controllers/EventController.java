package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.services.EventService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
public class EventController {
    private final EventService eventService;

    @GetMapping("/users/{id}/feed")
    public List<Event> findUserById(@PathVariable Long id) {
        log.info("GET /users/{}/feed - пользователь", id);
        return eventService.getEventsByUserId(id);
    }
}
