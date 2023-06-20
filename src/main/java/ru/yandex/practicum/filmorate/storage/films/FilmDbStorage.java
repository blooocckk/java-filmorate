package ru.yandex.practicum.filmorate.storage.films;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.likes.LikesStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final LikesStorage likesStorage;
    private final MpaStorage mpaStorage;
    private final JdbcTemplate jdbcTemplate;

    private LinkedHashSet<Genre> getGenresForFilm(Long filmId) {
        String sql = "SELECT g.GENRE_ID AS ID, g.NAME " +
                "FROM FILM_GENRE fg " +
                "JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID " +
                "WHERE fg.FILM_ID = ?";
        return new LinkedHashSet<>(jdbcTemplate.query(sql, (rs, rowNum) -> {
            Genre genre = new Genre();
            genre.setId(rs.getLong("ID"));
            genre.setName(rs.getString("NAME"));
            return genre;
        }, filmId));
    }

    private void setGenresAndMpa(Long filmId, Film film) {
        String sql = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(sql, filmId);

        sql = "MERGE INTO FILM_GENRE (FILM_ID, GENRE_ID) VALUES (?, ?)";
        List<Object[]> batchArgs = new ArrayList<>();
        for (Genre genre : film.getGenres()) {
            batchArgs.add(new Object[]{filmId, genre.getId()});
        }
        jdbcTemplate.batchUpdate(sql, batchArgs);

        LinkedHashSet<Genre> genres = getGenresForFilm(filmId);
        Mpa mpa = mpaStorage.getById(film.getMpa().getId());
        film.setGenres(new LinkedHashSet<>(genres));
        film.setMpa(mpa);
    }

    @Override
    public Film create(Film film) {
        String sql = "INSERT INTO FILMS (NAME, DESCRIPTION, RELEASEDATE, DURATION, RATING_ID) VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setDate(3, java.sql.Date.valueOf(film.getReleaseDate()));
            ps.setInt(4, film.getDuration());
            ps.setLong(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        Long generatedId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        film.setId(generatedId);
        setGenresAndMpa(generatedId, film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE FILMS SET NAME = ?, DESCRIPTION = ?, RELEASEDATE = ?, DURATION = ?, " +
                "RATING_ID = ? WHERE ID = ?";
        jdbcTemplate.update(sql, film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());
        setGenresAndMpa(film.getId(), film);
        return film;
    }

    @Override
    public Film getById(Long id) {
        String sql = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION, r.RATING_ID, r.NAME as RATING_NAME, " +
                "GROUP_CONCAT(g.GENRE_ID SEPARATOR ', ') AS GENRE_IDS, GROUP_CONCAT(g.NAME SEPARATOR ', ') AS GENRES " +
                "FROM FILMS f\n" +
                "LEFT JOIN RATING r ON f.RATING_ID = r.RATING_ID\n" +
                "LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID\n" +
                "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID\n" +
                "WHERE f.ID = ? " +
                "GROUP BY f.ID, r.RATING_ID, r.NAME; ";
        try {
            return jdbcTemplate.queryForObject(sql, new FilmRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new ObjectNotFoundException("Фильм не найден, ID: " + id);
        }
    }

    @Override
    public Collection<Film> getAll() {
        String sql = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION, r.RATING_ID, r.NAME as RATING_NAME, " +
                "GROUP_CONCAT(g.GENRE_ID SEPARATOR ', ') AS GENRE_IDS, GROUP_CONCAT(g.NAME SEPARATOR ', ') AS GENRES " +
                "FROM FILMS f\n" +
                "LEFT JOIN RATING r ON f.RATING_ID = r.RATING_ID\n" +
                "LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID\n" +
                "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID\n" +
                "GROUP BY f.ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION;";
        return jdbcTemplate.query(sql, new FilmRowMapper());
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        likesStorage.create(filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        likesStorage.delete(filmId, userId);
    }

    @Override
    public List<Film> getMostPopular(int count) {
        String sql = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASEDATE, f.DURATION, r.RATING_ID, r.NAME AS RATING_NAME, " +
                "GROUP_CONCAT(g.GENRE_ID SEPARATOR ', ') AS GENRE_IDS, GROUP_CONCAT(g.NAME SEPARATOR ', ') AS GENRES, " +
                "COUNT(l.USER_ID) AS NUM_LIKES " +
                "FROM FILMS f " +
                "LEFT JOIN RATING r ON f.RATING_ID = r.RATING_ID " +
                "LEFT JOIN LIKES l ON f.ID = l.FILM_ID " +
                "LEFT JOIN FILM_GENRE fg ON f.ID = fg.FILM_ID\n" +
                "LEFT JOIN GENRE g ON fg.GENRE_ID = g.GENRE_ID\n" +
                "GROUP BY f.ID, r.RATING_ID, r.NAME " +
                "ORDER BY NUM_LIKES DESC " +
                "LIMIT ?";
        return jdbcTemplate.query(sql, new FilmRowMapper(), count);
    }

    private static class FilmRowMapper implements RowMapper<Film> {
        @Override
        public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
            Film film = new Film();
            film.setId(rs.getLong("ID"));
            film.setName(rs.getString("NAME"));
            film.setDescription(rs.getString("DESCRIPTION"));
            film.setReleaseDate(rs.getDate("RELEASEDATE").toLocalDate());
            film.setDuration(rs.getInt("DURATION"));

            Mpa mpa = new Mpa();
            mpa.setId(rs.getLong("RATING_ID"));
            mpa.setName(rs.getString("RATING_NAME"));
            film.setMpa(mpa);

            String genresString = rs.getString("GENRES");
            LinkedHashSet<Genre> genres = new LinkedHashSet<>();

            if (genresString != null) {
                String[] genreIds = rs.getString("GENRE_IDS").split(", ");
                String[] genreNames = genresString.split(", ");
                for (int i = 0; i < genreIds.length; i++) {
                    Genre genre = new Genre();
                    genre.setId(Long.parseLong(genreIds[i]));
                    genre.setName(genreNames[i]);
                    genres.add(genre);
                }
            }

            film.setGenres(genres);
            return film;
        }
    }
}