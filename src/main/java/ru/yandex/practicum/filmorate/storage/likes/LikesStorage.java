package ru.yandex.practicum.filmorate.storage.likes;

import java.util.Set;

public interface LikesStorage {
    void create(Long filmId, Long userId);

    void update(Long filmId, Set<Long> likes);

    void delete(Long filmId, Long userId);

    Set<Long> getLikes(Long filmId);
}
