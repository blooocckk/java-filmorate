package ru.yandex.practicum.filmorate.storage.films;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component("InMemoryFilmStorage")
@Qualifier("memory")
public class InMemoryFilmStorage extends BaseStorage<Film> implements FilmStorage {
    private static final Comparator<Film> likesComparator = (o1, o2) -> (o2.getLikes().size() - o1.getLikes().size());

    @Override
    public void addLike(Long filmId, Long id) {
        getById(filmId).getLikes().add(id);
    }

    @Override
    public void removeLike(Long filmId, Long id) {
        if (!getById(filmId).getLikes().removeIf(e -> e.equals(id))) {
            throw new ObjectNotFoundException("Пользователь не найден, ID " + id);
        }
    }

    @Override
    public List<Film> getMostPopular(int count) {
        return getAll().stream()
                .sorted(likesComparator)
                .limit(count)
                .collect(Collectors.toList());
    }
}