package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

@Slf4j
@Component("InMemoryFilmStorage")
public class InMemoryFilmStorage extends BaseStorage<Film> implements FilmStorage {
    private void validation(Film film) {
        if (isCreated(film.getId())) {
            log.warn("Фильм не найден, ID {}", film.getId());
            throw new ObjectNotFoundException("Фильм не найден, ID " + film.getId());
        }
    }

    @Override
    public Film create(Film film) {
        log.info("Создание фильма выполнено");
        return super.create(film);
    }

    @Override
    public Film update(Film film) {
        validation(film);
        log.info("Обновление фильма выполнено");
        return super.update(film);
    }

    @Override
    public Collection<Film> getAll() {
        log.info("Возвращаем список всех фильмов");
        return super.getAll();
    }

    @Override
    public Film getFilmById(Long id) {
        log.info("Возвращаем фильм по ID");
        return super.getById(id);
    }

    @Override
    public void addLike(Long filmId, Long id) {
        getById(filmId).getLikes().add(id);
        log.info("Добавление лайка выполнено");
    }

    @Override
    public void removeLike(Long filmId, Long id) {
        if (!getById(filmId).getLikes().removeIf(e -> e.equals(id))) {
            log.warn("Пользователь не найден, ID " + id);
            throw new ObjectNotFoundException("Пользователь не найден, ID " + id);
        }
    }
}
