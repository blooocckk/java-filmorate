package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.AbstractModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
public abstract class BaseStorage<T extends AbstractModel> {
    private Long id = 1L;
    private final HashMap<Long, T> storage = new HashMap<>();

    public Collection<T> getAll() {
        return storage.values();
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

    protected T getById(Long id) {
        return Optional.ofNullable(storage.get(id))
                .orElseThrow(() -> {
                    log.warn("Объект не найден, ID " + id);
                    throw new ObjectNotFoundException("Объект не найден, ID " + id);
                });
    }

    protected boolean isCreated(Long checkedId) {
        return !storage.containsKey(checkedId);
    }
}
