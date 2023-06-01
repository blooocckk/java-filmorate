package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.films.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikesDbStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.users.UserDbStorage;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class FilmDbStorageIntegrationTest {
    private Film film;
    private Film film2;
    private User user;

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private MpaDbStorage mpaDbStorage;

    @Autowired
    private GenreDbStorage genreDbStorage;

    @Autowired
    private LikesDbStorage likesDbStorage;

    @Autowired
    private UserDbStorage userDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        jdbcTemplate.update("DELETE FROM FILMS");
        jdbcTemplate.update("DELETE FROM LIKES");
        jdbcTemplate.update("DELETE FROM FILM_GENRE");

        film = new Film();
        film.setName("Test Film");
        film.setDescription("Test Description");
        film.setReleaseDate(LocalDate.now());
        film.setDuration(120);

        Mpa mpa = new Mpa();
        mpa.setId(1L);
        film.setMpa(mpa);

        Genre genre = new Genre();
        genre.setId(1L);
        LinkedHashSet<Genre> genres = new LinkedHashSet<>();
        genres.add(genre);
        film.setGenres(genres);

        film2 = new Film();
        film2.setName("Second Film");
        film2.setDescription("Test Description 2");
        film2.setReleaseDate(LocalDate.now());
        film2.setDuration(90);

        mpa = new Mpa();
        mpa.setId(3L);
        film2.setMpa(mpa);

        genre = new Genre();
        genre.setId(4L);
        genres = new LinkedHashSet<>();
        genres.add(genre);
        film2.setGenres(genres);

        user = new User();
        user.setName("Block");
        user.setLogin("brick");
        user.setEmail("block@gmail.com");
        user.setBirthday(LocalDate.of(2002, 8, 6));
    }

    @Test
    void testCreateFilm() {
        Film createdFilm = filmDbStorage.create(film);

        assertNotNull(createdFilm.getId());
        assertEquals(film.getName(), createdFilm.getName());
        assertEquals(film.getDescription(), createdFilm.getDescription());
        assertEquals(film.getReleaseDate(), createdFilm.getReleaseDate());
        assertEquals(film.getDuration(), createdFilm.getDuration());
        assertEquals(film.getMpa().getId(), createdFilm.getMpa().getId());
        assertEquals(film.getGenres(), createdFilm.getGenres());
        assertTrue(filmDbStorage.getAll().contains(createdFilm));
    }

    @Test
    void testCreateFilmWithEmptyName() {
        film.setName("");
        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> filmDbStorage.create(film));
        assertEquals(filmDbStorage.getAll().size(), 0);
    }

    @Test
    void testCreateFilmWithDescriptionMoreThan200Char() {
        String maxDescription = "Test description Test description Test description \" +\n" +
                "                \"Test description Test description Test description \" +\n" +
                "                \"Test description Test description Test description \" +\n" +
                "                \"Test description Test description Test description";
        film.setDescription(maxDescription);

        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> filmDbStorage.create(film));
        assertEquals(filmDbStorage.getAll().size(), 0);
    }

    @Test
    void testCreateFilmWithZeroDuration() {
        film.setDuration(0);
        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> filmDbStorage.create(film));
        assertEquals(filmDbStorage.getAll().size(), 0);
    }

    @Test
    void testUpdateFilm() {
        Film createdFilm = filmDbStorage.create(film);

        createdFilm.setName("Updated Film");
        createdFilm.setDescription("Updated Description");
        createdFilm.setDuration(150);

        Mpa updatedMpa = new Mpa();
        updatedMpa.setId(2L);
        createdFilm.setMpa(updatedMpa);

        Genre updatedGenre = new Genre();
        updatedGenre.setId(2L);
        LinkedHashSet<Genre> updatedGenres = new LinkedHashSet<>();
        updatedGenres.add(updatedGenre);
        createdFilm.setGenres(updatedGenres);

        filmDbStorage.update(createdFilm);

        String sql = "SELECT * FROM FILMS WHERE ID = ?";
        Map<String, Object> result = jdbcTemplate.queryForMap(sql, film.getId());

        assertEquals(film.getName(), result.get("NAME"));
        assertEquals(film.getDescription(), result.get("DESCRIPTION"));
        assertEquals(film.getReleaseDate(), ((java.sql.Date) result.get("RELEASEDATE")).toLocalDate());
        assertEquals(film.getDuration(), result.get("DURATION"));
        assertEquals(film.getMpa(), mpaDbStorage.getById((Long) result.get("RATING_ID")));
    }

    @Test
    void testUpdateFilmWithEmptyName() {
        Film createdFilm = filmDbStorage.create(film);
        createdFilm.setName("");

        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> filmDbStorage.update(createdFilm));
        assertEquals(filmDbStorage.getAll().size(), 1);
    }

    @Test
    void testUpdateFilmWithDescriptionMoreThan200Char() {
        Film createdFilm = filmDbStorage.create(film);
        String maxDescription = "Test description Test description Test description \" +\n" +
                "                \"Test description Test description Test description \" +\n" +
                "                \"Test description Test description Test description \" +\n" +
                "                \"Test description Test description Test description";
        createdFilm.setDescription(maxDescription);

        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> filmDbStorage.create(createdFilm));
        assertEquals(filmDbStorage.getAll().size(), 1);
    }

    @Test
    void testUpdateFilmWithZeroDuration() {
        Film createdFilm = filmDbStorage.create(film);
        createdFilm.setDuration(0);

        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> filmDbStorage.update(createdFilm));
        assertEquals(filmDbStorage.getAll().size(), 1);
    }

    @Test
    void testUpdateNonExistentFilm() {
        film.setId(4L);

        DataIntegrityViolationException e = assertThrows(DataIntegrityViolationException.class,
                () -> filmDbStorage.update(film));
        assertEquals(filmDbStorage.getAll().size(), 0);
    }

    @Test
    void testGetById() {
        Film createdFilm = filmDbStorage.create(film);

        Film retrievedFilm = filmDbStorage.getById(createdFilm.getId());

        assertEquals(createdFilm.getId(), retrievedFilm.getId());
        assertEquals(createdFilm.getName(), retrievedFilm.getName());
        assertEquals(createdFilm.getDescription(), retrievedFilm.getDescription());
        assertEquals(createdFilm.getReleaseDate(), retrievedFilm.getReleaseDate());
        assertEquals(createdFilm.getDuration(), retrievedFilm.getDuration());
        assertEquals(createdFilm.getMpa().getId(), retrievedFilm.getMpa().getId());
        assertEquals(createdFilm.getGenres(), retrievedFilm.getGenres());
    }

    @Test
    void testGetByIdForNonExistentFilm() {
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> filmDbStorage.getById(9999L));
    }

    @Test
    void testGetAll() {
        Film createdFilm = filmDbStorage.create(film);
        Film createdFilm2 = filmDbStorage.create(film2);

        Collection<Film> films = filmDbStorage.getAll();

        assertTrue(films.contains(createdFilm));
        assertTrue(films.contains(createdFilm2));
        assertEquals(films.size(), 2);
    }

    @Test
    void testGetAllForEmptyDatabase() {
        Collection<Film> films = filmDbStorage.getAll();
        assertTrue(films.isEmpty());
    }

    @Test
    void testGetMostPopular() {
        Film createdFilm = filmDbStorage.create(film2);
        Film createdFilm2 = filmDbStorage.create(film);
        User createdUser = userDbStorage.create(user);
        filmDbStorage.addLike(createdFilm2.getId(), createdUser.getId());
        createdFilm2.setLikes(likesDbStorage.getLikes(film.getId()));

        List<Film> films = new ArrayList<>(filmDbStorage.getMostPopular(2));

        assertTrue(films.contains(createdFilm));
        assertTrue(films.contains(createdFilm2));
        assertEquals(films.size(), 2);
        assertEquals(films.get(0), createdFilm2);
        assertEquals(films.get(1), createdFilm);
    }

    @Test
    void testAddLike() {
        Film createdFilm = filmDbStorage.create(film);
        User createdUser = userDbStorage.create(user);
        filmDbStorage.addLike(createdFilm.getId(), createdUser.getId());
        Set<Long> likes = new HashSet<>();
        likes.add(user.getId());
        createdFilm.setLikes(likes);

        assertEquals(createdFilm.getLikes(), likesDbStorage.getLikes(film.getId()));
    }

    @Test
    void testRemoveLike() {
        Film createdFilm = filmDbStorage.create(film);
        User createdUser = userDbStorage.create(user);
        filmDbStorage.addLike(createdFilm.getId(), createdUser.getId());

        assertEquals(likesDbStorage.getLikes(film.getId()).size(), 1);

        filmDbStorage.removeLike(createdFilm.getId(), createdUser.getId());

        assertEquals(likesDbStorage.getLikes(film.getId()).size(), 0);
    }
}