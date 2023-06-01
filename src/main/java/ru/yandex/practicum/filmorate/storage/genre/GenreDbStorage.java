package ru.yandex.practicum.filmorate.storage.genre;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

@Repository
@Component
public class GenreDbStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public GenreDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Genre getById(Long id) {
        String sql = "SELECT * FROM GENRE WHERE GENRE_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new GenreRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Жанр не найден, ID: " + id);
        }
    }

    @Override
    public Collection<Genre> getAll() {
        String sql = "SELECT * FROM GENRE";
        return jdbcTemplate.query(sql, new GenreRowMapper());
    }

    @Override
    public List<Genre> getGenresForFilm(Long filmId) {
        String sql = "SELECT g.GENRE_ID, g.NAME " +
                "FROM PUBLIC.GENRE g " +
                "JOIN PUBLIC.FILM_GENRE fg ON g.GENRE_ID = fg.GENRE_ID " +
                "WHERE fg.FILM_ID = ?";
        return jdbcTemplate.query(sql, new GenreRowMapper(), filmId);
    }

    private static class GenreRowMapper implements RowMapper<Genre> {
        @Override
        public Genre mapRow(ResultSet rs, int rowNum) throws SQLException {
            Genre genre = new Genre();
            genre.setId(rs.getLong("GENRE_ID"));
            genre.setName(rs.getString("NAME"));
            return genre;
        }
    }
}