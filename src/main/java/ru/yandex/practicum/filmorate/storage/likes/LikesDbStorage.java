package ru.yandex.practicum.filmorate.storage.likes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Repository
public class LikesDbStorage implements LikesStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public LikesDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private boolean isLikeExists(Long filmId, Long userId) {
        String sql = "SELECT COUNT(*) FROM LIKES WHERE (FILM_ID = ? AND USER_ID = ?) OR FILM_ID = ? OR USER_ID = ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, filmId, userId, filmId, userId);
        return count != null && count > 0;
    }

    @Override
    public void create(Long filmId, Long userId) {
        String sql = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public void update(Long filmId, Set<Long> likes) {
        String sql = "DELETE FROM LIKES WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, filmId);
        sql = "INSERT INTO LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Long userId : likes) {
            batchArgs.add(new Object[]{filmId, userId});
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    @Override
    public void delete(Long filmId, Long userId) {
        if (!isLikeExists(filmId, userId)) {
            throw new ObjectNotFoundException("Такого лайка нет");
        }
        String sql = "DELETE FROM LIKES WHERE FILM_ID = ? AND USER_ID = ?";
        jdbcTemplate.update(sql, filmId, userId);
    }

    @Override
    public Set<Long> getLikes(Long filmId) {
        String query = "SELECT USER_ID FROM LIKES WHERE FILM_ID = ?";
        List<Long> likes = jdbcTemplate.queryForList(query, Long.class, filmId);
        return new HashSet<>(likes);
    }
}
