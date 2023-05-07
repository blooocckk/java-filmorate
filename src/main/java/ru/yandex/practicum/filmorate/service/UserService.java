package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public Collection<User> getAll() {
        return userStorage.getAll();
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public List<User> getFriends(Long id) {
        log.info("Возвращаем список друзей");
        return userStorage.getUserById(id).getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public void addAsFriend(Long userId, Long id) {
        Long friendId = userStorage.getUserById(id).getId();
        userStorage.addAsFriend(userId, friendId);
        userStorage.addAsFriend(friendId, userId);
    }

    public void removeFromFriends(Long userId, Long id) {
        userStorage.removeFromFriends(userId, id);
        userStorage.removeFromFriends(id, userId);
    }

    public List<User> getMutualFriends(Long userId, Long id) {
        log.info("Возвращаем список общих друзей");
        return userStorage.getUserById(userId).getFriends().stream()
                .filter(userStorage.getUserById(id).getFriends()::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}