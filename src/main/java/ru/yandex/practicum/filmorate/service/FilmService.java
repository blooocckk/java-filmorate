package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService extends AbstractService<Film> {

    private void validation(Film film) {
        try {
            super.getById(film.getId());
        } catch (ObjectNotFoundException e) {
            log.warn("Фильм не найден, ID {}", film.getId());
            throw new ObjectNotFoundException("Фильм не найден, ID " + film.getId());
        }
    }

    public Film create(Film film) {
        log.info("Создание фильма выполнено");
        return super.create(film);
    }

    public Film update(Film film) {
        validation(film);
        log.info("Обновление фильма выполнено");
        return super.update(film);
    }

    public Collection<Film> getAll() {
        log.info("Возвращаем список всех фильмов");
        return super.getAll();
    }

    public Film getFilmById(Long id) {
        log.info("Возвращаем фильм по ID");
        return super.getById(id);
    }

    public void addLike(Long filmId, Long id) {
        super.getById(filmId).getLikes().add(id);
        log.info("Добавление лайка выполнено");
    }

    public void removeLike(Long filmId, Long id) {
        if (!super.getById(filmId).getLikes().removeIf(e -> e.equals(id))) {
            log.warn("Пользователь не найден, ID " + id);
            throw new ObjectNotFoundException("Пользователь не найден, ID " + id);
        }
        log.info("Удаление лайка выполнено");
    }

    public List<Film> getMostPopular(int count) {
        log.info("Возвращаем список популярных фильмов");
        Comparator<Film> likesComparator = (o1, o2) -> (o2.getLikes().size() - o1.getLikes().size());
        return super.getAll().stream()
                .sorted(likesComparator)
                .limit(count)
                .collect(Collectors.toList());
    }
}