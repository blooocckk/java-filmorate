package ru.yandex.practicum.filmorate.storage.users;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.BaseStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component("InMemoryUserStorage")
@Qualifier("memory")
public class InMemoryUserStorage extends BaseStorage<User> implements UserStorage {
    @Override
    public void addAsFriend(Long userId, Long friendId) {
        Long friendIdTemp = getById(friendId).getId();
        getById(userId).getFriends().add(friendIdTemp);
        getById(friendIdTemp).getFriends().add(userId);
    }

    @Override
    public List<User> getFriends(Long id) {
        Set<Long> userFriends = getById(id).getFriends();
        return userFriends.stream()
                .map(this::getById)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getMutualFriends(Long userId, Long id) {
        Set<Long> firstUserFriends = getById(userId).getFriends();
        Set<Long> secondUserFriends = getById(id).getFriends();
        return firstUserFriends.stream()
                .filter(secondUserFriends::contains)
                .map(this::getById)
                .collect(Collectors.toList());
    }

    @Override
    public void removeFromFriends(Long userId, Long id) {
        getById(userId).getFriends().remove(id);
        getById(id).getFriends().remove(userId);
    }
}