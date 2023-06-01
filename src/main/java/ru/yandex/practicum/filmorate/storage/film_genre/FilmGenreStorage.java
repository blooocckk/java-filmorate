package ru.yandex.practicum.filmorate.storage.film_genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.LinkedHashSet;
import java.util.Set;

public interface FilmGenreStorage {
    void create(Long filmId, Set<Genre> genres);

    void update(Long filmId, Set<Genre> genres);

    void delete(Long filmId, Long genreId);

    LinkedHashSet<Genre> getGenresForFilm(Long filmId);
}
