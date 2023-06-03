package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void create(Long userId, Long friendId, boolean status) {
        String sql = "INSERT INTO FRIENDSHIP (USER1_ID, USER2_ID, STATUS) VALUES (?, ?, ?)";
        jdbcTemplate.update(sql, userId, friendId, status);
    }

    @Override
    public void update(Long userId, Long friendId, boolean status) {
        String sql = "UPDATE FRIENDSHIP SET STATUS = ? WHERE USER1_ID = ? AND USER2_ID = ?";
        jdbcTemplate.update(sql, status, userId, friendId);
    }

    @Override
    public void delete(Long userId, Long friendId) {
        String sql = "DELETE FROM FRIENDSHIP WHERE USER1_ID = ? AND USER2_ID = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    @Override
    public Set<Long> getUserFriends(Long userId) {
        String sql = "SELECT USER2_ID FROM FRIENDSHIP WHERE USER1_ID = ?";
        List<Long> friends = jdbcTemplate.queryForList(sql, Long.class, userId);
        return new HashSet<>(friends);
    }
}