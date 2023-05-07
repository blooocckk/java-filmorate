package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Film getFilmById(Long id);

    void addLike(Long filmId, Long id);

    void removeLike(Long filmId, Long id);

    Collection<Film> getAll();
}