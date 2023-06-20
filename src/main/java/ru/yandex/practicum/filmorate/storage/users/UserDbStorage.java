package ru.yandex.practicum.filmorate.storage.users;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendshipStorage friendshipStorage;

    @Override
    public User create(User user) {
        String query = "INSERT INTO USERS (EMAIL, LOGIN, NAME, BIRTHDAY) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setDate(4, java.sql.Date.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);
        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        user.setId(generatedId);
        return user;
    }

    @Override
    public User update(User user) {
        String query = "UPDATE USERS SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? WHERE ID = ?";
        jdbcTemplate.update(query, user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId());
        return user;
    }

    @Override
    public User getById(Long id) {
        String query = "SELECT u.ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY FROM USERS u WHERE ID = ?";
        try {
            return jdbcTemplate.queryForObject(query, new UserRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Пользователь не найден, ID: " + id);
        }
    }

    @Override
    public void addAsFriend(Long userId, Long friendId) {
        boolean friendshipStatus = false;
        Set<Long> friendsOfFriend = friendshipStorage.getUserFriends(friendId);
        if (friendsOfFriend.contains(userId)) {
            friendshipStatus = true;
            friendshipStorage.update(friendId, userId, true);
        }
        friendshipStorage.create(userId, friendId, friendshipStatus);
    }

    @Override
    public List<User> getAll() {
        String query = "SELECT u.ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY FROM USERS u";
        return jdbcTemplate.query(query, new UserRowMapper());
    }

    @Override
    public void delete(User user) {
        String query = "DELETE FROM USERS WHERE ID = ?";
        jdbcTemplate.update(query, user.getId());
    }

    @Override
    public List<User> getFriends(Long id) {
        String query = "SELECT u.ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY " +
                "FROM USERS u " +
                "JOIN FRIENDSHIP f ON u.ID = f.USER2_ID " +
                "WHERE f.USER1_ID = ?;";
        return jdbcTemplate.query(query, new UserRowMapper(), id);
    }

    @Override
    public List<User> getMutualFriends(Long userId, Long friendId) {
        String query = "SELECT u.ID, u.EMAIL, u.LOGIN, u.NAME, u.BIRTHDAY " +
                "FROM friendship AS f1 " +
                "JOIN friendship AS f2 ON f1.USER2_ID  = f2.USER2_ID  AND f1.USER1_ID = ? AND f2.USER1_ID = ? " +
                "JOIN users AS u ON f1.USER2_ID  = u.id;";
        return jdbcTemplate.query(query, new UserRowMapper(), userId, friendId);
    }

    @Override
    public void removeFromFriends(Long userId, Long id) {
        friendshipStorage.delete(userId, id);
        friendshipStorage.delete(id, userId);
    }

    private static class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("ID"));
            user.setEmail(rs.getString("EMAIL"));
            user.setLogin(rs.getString("LOGIN"));
            user.setName(rs.getString("NAME"));
            user.setBirthday(rs.getDate("BIRTHDAY").toLocalDate());
            return user;
        }
    }
}
