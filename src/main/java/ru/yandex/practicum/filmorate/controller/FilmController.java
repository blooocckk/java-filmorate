package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.*;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends BaseController<Film> {
    private void validation(Film film) {
        if (isCreated(film.getId())) {
            log.warn("Фильм не найден, ID: {}", film.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм не найден");
        }
    }

    @GetMapping
    public Collection<Film> getAll() {
        log.info("Получен запрос на получение всех фильмов");
        return super.getAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Получен запрос на создание фильма");
        return super.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Получен запрос на обновление фильма");
        validation(film);
        return super.update(film);
    }

    @Override
    protected void setId(int id, Film film) {
        film.setId(id);
    }

    @Override
    protected int getId(Film film) {
        return film.getId();
    }
}