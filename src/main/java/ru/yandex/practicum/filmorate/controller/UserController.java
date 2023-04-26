package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private static int userId = 1;
    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();
    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            log.error("Невалидные данные, попробуйте снова");
            return user;
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            log.info("Имя не задано, указали логин");
            user.setName(user.getLogin());
        }
        user.setId(userId);
        userId++;
        users.put(user.getId(), user);
        log.info("Пользователь создан");
        return user;
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        if (!users.containsKey(user.getId())) {
            log.warn("Пользователь не найден, ID: {}", user.getId());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь не найден");
        }
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            log.error("Невалидные данные, попробуйте снова");
            return user;
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            log.info("Имя не задано, указали логин");
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь обновлен");
        return user;
    }
}