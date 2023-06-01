package ru.yandex.practicum.filmorate.storage.friendship;

import java.util.Set;

public interface FriendshipStorage {
    void create(Long userId, Long friendId, boolean status);

    void update(Long userId, Long friendId, boolean status);

    void delete(Long userId, Long friendId);

    Set<Long> getUserFriends(Long userId);
}
