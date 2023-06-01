package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.AbstractModel;

import java.util.*;

public abstract class BaseStorage<T extends AbstractModel> {
    private Long id = 1L;
    private final HashMap<Long, T> storage = new HashMap<>();

    public Collection<T> getAll() {
        return List.copyOf(storage.values());
    }

    public T create(T t) {
        t.setId(id);
        storage.put(id, t);
        id++;
        return t;
    }

    public T update(T t) {
        storage.put(t.getId(), t);
        return t;
    }

    public void delete(T t) {
        storage.remove(t.getId(), t);
    }

    public T getById(Long id) {
        return Optional.ofNullable(storage.get(id))
                .orElseThrow(() -> {
                    throw new ObjectNotFoundException("Объект не найден, ID " + id);
                });
    }
}
