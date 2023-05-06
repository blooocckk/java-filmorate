package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.*;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController extends BaseController<User> {
    private void validation(User user, boolean isUpdate) {
        if (isUpdate) {
            if (isCreated(user.getId())) {
                log.warn("Пользователь не найден, ID: {}", user.getId());
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
            }
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    @GetMapping
    public Collection<User> getAll() {
        log.info("Получен запрос на получение всех пользователей");
        return super.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя");
        validation(user, false);
        return super.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя");
        validation(user, true);
        return super.update(user);
    }

    @Override
    protected void setId(int id, User user) {
        user.setId(id);
    }

    @Override
    protected int getId(User user) {
        return user.getId();
    }
}