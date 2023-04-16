package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    Long eventId;
    Long entityId;
    String eventType;
    String operation;
    Long userId;
    Long timestamp;
}
