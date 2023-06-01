package ru.yandex.practicum.filmorate.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@Transactional
public class MpaDbStorageIntegrationTest {
    private Mpa mpa1;
    private Mpa mpa2;
    private Mpa mpa3;
    private Mpa mpa4;
    private Mpa mpa5;

    @Autowired
    private MpaDbStorage mpaDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        mpa1 = new Mpa();
        mpa1.setId(1L);
        mpa1.setName("G");
        mpa2 = new Mpa();
        mpa2.setId(2L);
        mpa2.setName("PG");
        mpa3 = new Mpa();
        mpa3.setId(3L);
        mpa3.setName("PG-13");
        mpa4 = new Mpa();
        mpa4.setId(4L);
        mpa4.setName("R");
        mpa5 = new Mpa();
        mpa5.setId(5L);
        mpa5.setName("NC-17");
    }

    @Test
    void testGetById() {
        Mpa retrievedMpa = mpaDbStorage.getById(1L);

        assertEquals(retrievedMpa, mpa1);
    }

    @Test
    void testGetByIdForNonExistentMpa() {
        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> mpaDbStorage.getById(9999L));
    }

    @Test
    void testGetAll() {
        Collection<Mpa> mpas = mpaDbStorage.getAll();

        assertTrue(mpas.contains(mpa1));
        assertTrue(mpas.contains(mpa2));
        assertTrue(mpas.contains(mpa3));
        assertTrue(mpas.contains(mpa4));
        assertTrue(mpas.contains(mpa5));
        assertEquals(mpas.size(), 5);
    }
}