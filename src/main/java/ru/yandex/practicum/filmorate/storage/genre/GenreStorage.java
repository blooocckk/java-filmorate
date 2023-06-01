package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;

public interface GenreStorage {
    Genre getById(Long id);

    Collection<Genre> getAll();

    List<Genre> getGenresForFilm(Long filmId);
}