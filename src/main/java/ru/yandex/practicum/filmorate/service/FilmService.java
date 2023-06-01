package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.films.FilmStorage;

import java.util.*;

@Service
@Slf4j
public class FilmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(@Qualifier("database") FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    private void validation(Film film) {
        try {
            filmStorage.getById(film.getId());
        } catch (ObjectNotFoundException e) {
            log.warn("Фильм не найден, ID {}", film.getId());
            throw new ObjectNotFoundException("Фильм не найден, ID " + film.getId());
        }
    }

    public Film create(Film film) {
        log.info("Создание фильма выполнено");
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validation(film);
        log.info("Обновление фильма выполнено");
        return filmStorage.update(film);
    }

    public Collection<Film> getAll() {
        log.info("Возвращаем список всех фильмов");
        return filmStorage.getAll();
    }

    public Film getFilmById(Long id) {
        log.info("Возвращаем фильм по ID");
        return filmStorage.getById(id);
    }

    public void addLike(Long filmId, Long id) {
        filmStorage.addLike(filmId, id);
        log.info("Добавление лайка выполнено");
    }

    public void removeLike(Long filmId, Long id) {
        filmStorage.removeLike(filmId, id);
        log.info("Удаление лайка выполнено");
    }

    public List<Film> getMostPopular(int count) {
        log.info("Возвращаем список популярных фильмов");
        return filmStorage.getMostPopular(count);
    }
}