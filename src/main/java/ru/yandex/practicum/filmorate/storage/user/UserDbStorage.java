package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.Collection;
import java.util.Optional;

@Slf4j
@Component
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Collection<User> findAllUsers() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    @Override
    public Collection<User> findBothUserFriends(Long user1, Long user2) {
        String sql = "SELECT * FROM users " +
                "WHERE user_id IN " +
                "(SELECT friend_id " +
                "FROM friends WHERE user_id=? " +
                "INTERSECT " +
                "SELECT friend_id " +
                "FROM friends WHERE user_id=?)";
        return jdbcTemplate.query(sql, this::makeUser, user1, user2);
    }


    @Override
    public Optional<User> findUserById(Long userId) {
        String sql = "SELECT * FROM users WHERE user_id=?";
        try {
            return Optional.of(jdbcTemplate.queryForObject(sql, this::makeUser, userId));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public User createUser(User user) {
        String sql = "INSERT INTO users (login, user_name, email, birthday) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getLogin());
            ps.setString(2, user.getName());
            ps.setString(3, user.getEmail());
            ps.setDate(4, Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        Long key = keyHolder.getKey().longValue();
        user.setId(key);
        log.debug("добавлен пользователь с id={}", key);
        return user;
    }

    @Override
    public User updateUser(User user) {
        String sql = "UPDATE users SET login=?, user_name=?, email=?, birthday=? WHERE user_id=?";
        int id = jdbcTemplate.update(sql, user.getLogin(), user.getName(), user.getEmail(),
                user.getBirthday(), user.getId());
        if (id == 1) {
            return user;
        } else {
            throw new NotFoundException(String.format("Пользователя с id=%d не существует.", user.getId()));
        }
    }

    @Override
    public void addFriend(Long userId, Long friendId) {
        String sql = "INSERT INTO friends (user_id, friend_id) " +
                "VALUES (?, ?)";
        try {
            jdbcTemplate.update(sql, userId, friendId);
        } catch (DataIntegrityViolationException e) {
            throw new ValidationException("Нарушение ссылочной целостности");
        }
    }

    @Override
    public void removeFriend(Long userId, Long friendId) {
        String sql = "DELETE FROM friends WHERE user_id=? AND friend_id=?";
        int res = jdbcTemplate.update(sql, userId, friendId);
        if (res != 1) {
            throw new NotFoundException(String.format("Пользователя с id=%d не существует.", friendId));
        }
    }

    @Override
    public Collection<User> findFriends(Long userId) {
        String sql = "SELECT * FROM users " +
                "WHERE user_id IN " +
                "(SELECT friend_id " +
                "FROM friends WHERE user_id=?)";
        return jdbcTemplate.query(sql, this::makeUser, userId);
    }

    @Override
    public Boolean checkUser(Long userId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE user_id=?)";
        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> rs.getBoolean(1), userId);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("user_id"))
                .login(rs.getString("login"))
                .name(rs.getString("user_name"))
                .email(rs.getString("email"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
        return user;
    }
}
