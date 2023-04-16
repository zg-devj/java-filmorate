package ru.yandex.practicum.filmorate.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.ValidateUtil;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class EventService {
    private final EventStorage eventStorage;
    private final UserStorage userStorage;

    public List<Event> getEventsByUserId(Long id) {
        if (!userStorage.checkUser(id)) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", id));
        }
        return eventStorage.getEventsByUserId(id);
    }

    public void addEvent(Long userId, Long entityId, EventStorage.TypeName type, EventStorage.OperationName operation) {
        eventStorage.addEvent(userId, entityId, type, operation);
    }

    public void removeEventsByUserId(Long id) {
        if (!userStorage.checkUser(id)) {
            ValidateUtil.throwNotFound(String.format("Пользователь с %d не найден.", id));
        }
        eventStorage.removeEventsByUserId(id);
    }
}
