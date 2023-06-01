package ru.yandex.practicum.filmorate.storage.films;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Film getById(Long id);

    Collection<Film> getAll();

    void addLike(Long filmId, Long id);

    void removeLike(Long filmId, Long id);

    List<Film> getMostPopular(int count);
}