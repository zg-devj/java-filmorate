package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    enum TypeName {
        LIKE,
        REVIEW,
        FRIEND
    }

    enum OperationName {
        REMOVE,
        ADD,
        UPDATE
    }

    List<Event> getEventsByUserId(Long id);

    void addEvent(Long userId, Long entityId, TypeName type, OperationName operation);

    void removeEventsByUserId(Long id);

    void removeEventsByUserEntityId(Long id);

    void removeEventsByFilmEntityId(Long id);
}
