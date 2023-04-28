package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;

import java.util.Collection;
import java.util.HashMap;

@Slf4j
public abstract class BaseController<T> {
    private int id = 1;
    private final HashMap<Integer, T> storage = new HashMap<>();

    public Collection<T> getAll() {
        return storage.values();
    }

    public T create(T t) {
        setId(id, t);
        storage.put(id, t);
        id++;
        log.info("Создание выполнено");
        return t;
    }

    public T update(T t) {
        storage.put(getId(t), t);
        log.info("Обновление выполнено");
        return t;
    }

    protected boolean isCreated(int checkedId) {
        return !storage.containsKey(checkedId);
    }

    protected abstract void setId(int id, T t);

    protected abstract int getId(T t);
}
