package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

@Component("InMemoryUserStorage")
public class InMemoryUserStorage extends BaseStorage<User> implements UserStorage {

}