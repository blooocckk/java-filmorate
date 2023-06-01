package ru.yandex.practicum.filmorate.storage.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@Qualifier("database")
public class UserDbStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;
    private final FriendshipStorage friendshipStorage;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate, FriendshipStorage friendshipStorage) {
        this.jdbcTemplate = jdbcTemplate;
        this.friendshipStorage = friendshipStorage;
    }

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
        String query = "SELECT * FROM USERS WHERE ID = ?";
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
        String query = "SELECT * FROM USERS";
        return jdbcTemplate.query(query, new UserRowMapper());
    }

    @Override
    public void delete(User user) {
        String query = "DELETE FROM USERS WHERE ID = ?";
        jdbcTemplate.update(query, user.getId());
    }

    @Override
    public List<User> getFriends(Long id) {
        Set<Long> userFriends = friendshipStorage.getUserFriends(id);
        return userFriends.stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getMutualFriends(Long userId, Long friendId) {
        Set<Long> firstUserFriends = friendshipStorage.getUserFriends(userId);
        Set<Long> secondUserFriends = friendshipStorage.getUserFriends(friendId);
        return firstUserFriends.stream()
                .filter(secondUserFriends::contains)
                .map(this::getById)
                .collect(Collectors.toList());
    }

    @Override
    public void removeFromFriends(Long userId, Long id) {
        friendshipStorage.delete(userId, id);
        friendshipStorage.delete(id, userId);
    }

    private class UserRowMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId(rs.getLong("ID"));
            user.setEmail(rs.getString("EMAIL"));
            user.setLogin(rs.getString("LOGIN"));
            user.setName(rs.getString("NAME"));
            user.setBirthday(rs.getDate("BIRTHDAY").toLocalDate());
            user.setFriends(new HashSet<>(friendshipStorage.getUserFriends(user.getId())));
            return user;
        }
    }
}
