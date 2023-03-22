package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;

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
    public User findUserById(Long id) {
        String sql = "SELECT * FROM users WHERE user_id=?";
        try {
            return jdbcTemplate.queryForObject(sql, this::makeUser, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
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

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("user_id"))
                .login(rs.getString("login"))
                .name(rs.getString("user_name"))
                .email(rs.getString("email"))
                .friends(new HashSet<>())
                .filmsLike(new HashSet<>())
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();
        return user;
    }
}
