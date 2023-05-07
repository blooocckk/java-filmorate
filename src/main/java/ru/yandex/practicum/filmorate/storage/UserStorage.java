package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {
    User create(User user);

    User update(User user);

    Collection<User> getAll();

    User getUserById(Long id);

    void addAsFriend(Long userId, Long id);

    void removeFromFriends(Long userId, Long id);
}
