package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private static int filmId = 1;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            log.error("Невалидные данные, попробуйте снова");
            return film;
        }
        film.setId(filmId);
        filmId++;
        films.put(film.getId(), film);
        log.info("Фильм создан");
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Фильм не найден, ID: {}", film.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден");
        }
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            log.error("Невалидные данные, попробуйте снова");
            return film;
        }
        films.put(film.getId(), film);
        log.info("Фильм обновлен");
        return film;
    }
}