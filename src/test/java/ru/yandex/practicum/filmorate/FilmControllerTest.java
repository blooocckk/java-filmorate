package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.films.InMemoryFilmStorage;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class FilmControllerTest {
    private FilmController filmController;
    private Film film;
    private Validator validator;

    @BeforeEach
    void setUp() {
        InMemoryFilmStorage inMemoryFilmStorage = new InMemoryFilmStorage();
        FilmService filmService = new FilmService(inMemoryFilmStorage);
        filmController = new FilmController(filmService);
        film = new Film();
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Test
    void testPostFilm() {
        film.setId(1L);
        film.setName("Morning Bell");
        film.setDescription("DescriptionTest");
        film.setReleaseDate(LocalDate.of(2019, 12, 1));
        film.setDuration(120);

        assertEquals(filmController.create(film), film, "Созданный фильм отличается");
        assertEquals(filmController.getAll().size(), 1, "Фильм не добавлен в коллекцию");
    }

    @Test
    void testPostInvalidFilm() {
        filmController.create(film);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(2, violations.size(), "Валидация прошла успешно");
    }

    @Test
    void testPostFilmWithoutName() {
        film.setId(1L);
        film.setDescription("DescriptionTest");
        film.setReleaseDate(LocalDate.of(2019, 12, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Название не может быть пустым", violation.getMessage());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    void testPostFilmWithEmptyName() {
        film.setId(1L);
        film.setName("");
        film.setDescription("DescriptionTest");
        film.setReleaseDate(LocalDate.of(2019, 12, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Название не может быть пустым", violation.getMessage());
        assertEquals("name", violation.getPropertyPath().toString());
    }

    @Test
    void testPostFilmWithDescriptionMoreThan200Char() {
        film.setId(1L);
        film.setName("Morning Bell");
        film.setDescription("Test description Test description Test description " +
                "Test description Test description Test description " +
                "Test description Test description Test description " +
                "Test description Test description Test description");
        film.setReleaseDate(LocalDate.of(2019, 12, 1));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Максимальная длина описания — 200 символов", violation.getMessage());
        assertEquals("description", violation.getPropertyPath().toString());
    }

    @Test
    void testPostFilmWithEmptyDescription() {
        film.setId(1L);
        film.setName("Morning Bell");
        film.setDescription("");
        film.setReleaseDate(LocalDate.of(2019, 12, 1));
        film.setDuration(120);

        assertEquals(filmController.create(film), film, "Созданный фильм отличается");
        assertEquals(filmController.getAll().size(), 1, "Фильм не добавлен в коллекцию");
    }

    @Test
    void testPostFilmWithoutDescription() {
        film.setId(1L);
        film.setName("Morning Bell");
        film.setReleaseDate(LocalDate.of(2019, 12, 1));
        film.setDuration(120);

        assertEquals(filmController.create(film), film, "Созданный фильм отличается");
        assertEquals(filmController.getAll().size(), 1, "Фильм не добавлен в коллекцию");
    }

    @Test
    void testPostFilmWithIncorrectDate() {
        film.setId(1L);
        film.setName("Morning Bell");
        film.setDescription("DescriptionTest");
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        film.setDuration(120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Дата релиза не может быть раньше 1895-12-28", violation.getMessage());
        assertEquals("releaseDate", violation.getPropertyPath().toString());
    }

    @Test
    void testPostFilmWithDate1895_12_28() {
        film.setId(1L);
        film.setName("Morning Bell");
        film.setDescription("DescriptionTest");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        film.setDuration(120);

        assertEquals(filmController.create(film), film, "Созданный фильм отличается");
        assertEquals(filmController.getAll().size(), 1, "Фильм не добавлен в коллекцию");
    }

    @Test
    void testPostFilmWithEmptyDate() {
        film.setId(1L);
        film.setName("Morning Bell");
        film.setDescription("DescriptionTest");
        film.setDuration(120);

        assertEquals(filmController.create(film), film, "Созданный фильм отличается");
        assertEquals(filmController.getAll().size(), 1, "Фильм не добавлен в коллекцию");
    }

    @Test
    void testPostFilmWithNegativeDuration() {
        film.setId(1L);
        film.setName("Morning Bell");
        film.setDescription("DescriptionTest");
        film.setDuration(-120);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Продолжительность фильма должна быть положительной", violation.getMessage());
        assertEquals("duration", violation.getPropertyPath().toString());
    }

    @Test
    void testPostFilmWithZeroDuration() {
        film.setId(1L);
        film.setName("Morning Bell");
        film.setDescription("DescriptionTest");
        film.setDuration(0);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Продолжительность фильма должна быть положительной", violation.getMessage());
        assertEquals("duration", violation.getPropertyPath().toString());
    }

    @Test
    void testPostFilmWithoutDuration() {
        film.setId(1L);
        film.setName("Morning Bell");
        film.setDescription("DescriptionTest");
        film.setReleaseDate(LocalDate.of(1895, 12, 28));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertEquals(1, violations.size());
        ConstraintViolation<Film> violation = violations.iterator().next();
        assertEquals("Продолжительность фильма должна быть положительной", violation.getMessage());
        assertEquals("duration", violation.getPropertyPath().toString());
    }

    @Test
    void testUpdateFilm() {
        film.setId(1L);
        film.setName("Morning Bell");
        film.setDescription("DescriptionTest");
        film.setReleaseDate(LocalDate.of(2019, 12, 1));
        film.setDuration(120);
        filmController.create(film);

        film.setName("Home alone");
        filmController.update(film);

        assertEquals(filmController.getAll().size(), 1, "Размер коллекции изменился");
        assertTrue(filmController.getAll().contains(film), "Фильм в коллекции отличается");
    }

    @Test
    void testUpdateFilmWithIncorrectData() {
        film.setId(1L);
        film.setName("Morning Bell");
        film.setDescription("DescriptionTest");
        film.setReleaseDate(LocalDate.of(2019, 12, 1));
        film.setDuration(120);
        filmController.create(film);

        film.setName(" ");
        film.setDuration(-12);
        filmController.update(film);

        assertEquals(filmController.getAll().size(), 1, "Размер коллекции изменился");
        assertTrue(filmController.getAll().contains(film), "Фильм в коллекции отличается");
    }

    @Test
    void testUpdateNonExistentFilm() {
        film.setId(4L);

        ObjectNotFoundException e = assertThrows(ObjectNotFoundException.class,
                () -> filmController.update(film));
        assertEquals(filmController.getAll().size(), 0, "Размер коллекции изменился");
    }
}