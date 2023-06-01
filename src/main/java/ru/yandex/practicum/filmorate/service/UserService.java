package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.users.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(@Qualifier("database") UserStorage userStorage) {
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
        return userStorage.getFriends(id);
    }

    public void addAsFriend(Long userId, Long id) {
        userStorage.addAsFriend(userId, id);
        log.info("Добавление в друзья выполнено");
    }

    public void removeFromFriends(Long userId, Long id) {
        log.info("Удаление из друзей выполнено");
        userStorage.removeFromFriends(userId, id);
    }

    public List<User> getMutualFriends(Long userId, Long id) {
        log.info("Возвращаем список общих друзей");
        Set<Long> firstUserFriends = userStorage.getById(userId).getFriends();
        Set<Long> secondUserFriends = userStorage.getById(id).getFriends();
        return userStorage.getMutualFriends(userId, id);
    }
}