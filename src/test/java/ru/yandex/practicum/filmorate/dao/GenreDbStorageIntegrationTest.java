package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class GenreDbStorageIntegrationTest {
    private Genre genre1;
    private Genre genre2;
    private Genre genre3;
    private Genre genre4;
    private Genre genre5;
    private Genre genre6;

    @Autowired
    private GenreDbStorage genreDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        genre1 = new Genre();
        genre1.setId(1L);
        genre1.setName("Комедия");
        genre2 = new Genre();
        genre2.setId(2L);
        genre2.setName("Драма");
        genre3 = new Genre();
        genre3.setId(3L);
        genre3.setName("Мультфильм");
        genre4 = new Genre();
        genre4.setId(4L);
        genre4.setName("Триллер");
        genre5 = new Genre();
        genre5.setId(5L);
        genre5.setName("Документальный");
        genre6 = new Genre();
        genre6.setId(6L);
        genre6.setName("Боевик");
    }

    @Test
    void testGetById() {
        Genre retrievedGenre = genreDbStorage.getById(1L);

        assertEquals(retrievedGenre, genre1);
    }

    @Test
    void testGetByIdForNonExistentGenre() {
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> genreDbStorage.getById(9999L));
    }

    @Test
    void testGetAll() {
        Collection<Genre> genres = genreDbStorage.getAll();

        assertEquals(genres.size(), 6);
        assertTrue(genres.contains(genre1));
        assertTrue(genres.contains(genre2));
        assertTrue(genres.contains(genre3));
        assertTrue(genres.contains(genre4));
        assertTrue(genres.contains(genre5));
        assertTrue(genres.contains(genre6));
    }
}