package ru.yandex.practicum.filmorate.storage.mpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Repository
@Component
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MpaDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Mpa getById(Long id) {
        String query = "SELECT * FROM RATING WHERE RATING_ID = ?";
        try {
            return jdbcTemplate.queryForObject(query, new MpaRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("MPA не найден, ID: " + id);
        }
    }

    @Override
    public Collection<Mpa> getAll() {
        String query = "SELECT * FROM RATING";
        return jdbcTemplate.query(query, new MpaRowMapper());
    }

    private static class MpaRowMapper implements RowMapper<Mpa> {
        @Override
        public Mpa mapRow(ResultSet rs, int rowNum) throws SQLException {
            Mpa mpa = new Mpa();
            mpa.setId(rs.getLong("RATING_ID"));
            mpa.setName(rs.getString("NAME"));
            return mpa;
        }
    }
}
