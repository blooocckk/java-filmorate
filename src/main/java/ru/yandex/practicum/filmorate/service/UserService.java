package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    private void validation(User user, boolean isUpdate) {
        if (isUpdate) {
            try {
                userStorage.getById(user.getId());
            } catch (ObjectNotFoundException e) {
                log.warn("Пользователь не найден, ID {}", user.getId());
                throw new ObjectNotFoundException("Пользователь не найден, ID " + user.getId());
            }
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    public User create(User user) {
        validation(user, false);
        log.info("Создание пользователя выполнено");
        return userStorage.create(user);
    }

    public User update(User user) {
        validation(user, true);
        log.info("Обновление пользователя выполнено");
        return userStorage.update(user);
    }

    public Collection<User> getAll() {
        log.info("Возвращаем список всех пользователей");
        return userStorage.getAll();
    }

    public User getUserById(Long id) {
        log.info("Возвращаем пользователя");
        return userStorage.getById(id);
    }

    public List<User> getFriends(Long id) {
        log.info("Возвращаем список друзей");
        Set<Long> userFriends = userStorage.getById(id).getFriends();
        return userFriends.stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    public void addAsFriend(Long userId, Long id) {
        Long friendId = userStorage.getById(id).getId();
        userStorage.getById(userId).getFriends().add(friendId);
        userStorage.getById(friendId).getFriends().add(userId);
        log.info("Добавление в друзья выполнено");
    }

    public void removeFromFriends(Long userId, Long id) {
        userStorage.getById(userId).getFriends().remove(id);
        userStorage.getById(id).getFriends().remove(userId);
        log.info("Удаление из друзей выполнено");
    }

    public List<User> getMutualFriends(Long userId, Long id) {
        log.info("Возвращаем список общих друзей");
        Set<Long> firstUserFriends = userStorage.getById(userId).getFriends();
        Set<Long> secondUserFriends = userStorage.getById(id).getFriends();
        return firstUserFriends.stream()
                .filter(secondUserFriends::contains)
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }
}