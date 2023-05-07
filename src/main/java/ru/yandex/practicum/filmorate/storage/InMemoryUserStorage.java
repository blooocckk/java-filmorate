package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

@Slf4j
@Component("InMemoryUserStorage")
public class InMemoryUserStorage extends BaseStorage<User> implements UserStorage {
    private void validation(User user, boolean isUpdate) {
        if (isUpdate) {
            if (isCreated(user.getId())) {
                log.warn("Пользователь не найден, ID {}", user.getId());
                throw new ObjectNotFoundException("Пользователь не найден, ID " + user.getId());
            }
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    @Override
    public User create(User user) {
        validation(user, false);
        log.info("Создание пользователя выполнено");
        return super.create(user);
    }

    @Override
    public User update(User user) {
        validation(user, true);
        log.info("Обновление пользователя выполнено");
        return super.update(user);
    }

    @Override
    public Collection<User> getAll() {
        log.info("Возвращаем список всех пользователей");
        return super.getAll();
    }

    @Override
    public User getUserById(Long id) {
        log.info("Возвращаем пользователя");
        return getById(id);
    }

    @Override
    public void addAsFriend(Long userId, Long id) {
        getById(userId).getFriends().add(id);
        log.info("Добавление в друзья выполнено");
    }

    @Override
    public void removeFromFriends(Long userId, Long id) {
        getById(userId).getFriends().remove(id);
        log.info("Удаление из друзей выполнено");
    }
}
