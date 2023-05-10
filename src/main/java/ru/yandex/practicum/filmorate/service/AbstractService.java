package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.AbstractModel;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.Collection;

public abstract class AbstractService<T extends AbstractModel> {
    @Autowired
    protected BaseStorage<T> baseStorage;

    public T create(T t) {
        return baseStorage.create(t);
    }

    public T update(T t) {
        return baseStorage.update(t);
    }

    public Collection<T> getAll() {
        return baseStorage.getAll();
    }

    public T getById(Long id) {
        return baseStorage.getById(id);
    }
}
