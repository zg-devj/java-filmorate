package ru.yandex.practicum.filmorate.storage.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getEventsByUserId(Long id) {
        String sql = "SELECT event_id, timestamp, event_type, operation, user_id, entity_id " +
                "FROM events WHERE user_id=" + id + " ORDER BY timestamp ASC";
        return jdbcTemplate.query(sql, BeanPropertyRowMapper.newInstance(Event.class));
    }

    @Override
    public void addEvent(Long userId, Long entityId, TypeName type, OperationName operation) {
        Long timestamp = Date.from(Instant.now()).getTime();
        String sql = String.format("INSERT INTO events (timestamp, event_type, operation, user_id, entity_id) " +
                "VALUES('%s', '%s', '%s', %d, %d)", timestamp, type, operation, userId, entityId);
        jdbcTemplate.execute(sql);
    }

    @Override
    public void removeEventsByUserId(Long id) {
        String sql = "DELETE FROM events WHERE user_id=" + id;
        jdbcTemplate.execute(sql);
    }

    @Override
    public void removeEventsByUserEntityId(Long id) {
        String sql = "DELETE FROM events WHERE event_type='FRIEND' AND entity_id=" + id;
        jdbcTemplate.execute(sql);
    }

    @Override
    public void removeEventsByFilmEntityId(Long id) {
        String sql = "DELETE FROM events WHERE event_type='LIKE' AND entity_id=" + id;
        jdbcTemplate.execute(sql);
    }
}
