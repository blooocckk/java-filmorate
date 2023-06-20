package ru.yandex.practicum.filmorate.storage.users;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(User user);

    void delete(User user);

    Collection<User> getAll();

    User getById(Long id);

    void addAsFriend(Long userId, Long friendID);

    List<User> getFriends(Long userId);

    List<User> getMutualFriends(Long userId, Long friendId);

    void removeFromFriends(Long userId, Long id);
}