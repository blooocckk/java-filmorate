package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.*;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> getAll() {
        log.info("Получен запрос на получение всех пользователей");
        return userService.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Получен запрос на создание пользователя");
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Получен запрос на обновление пользователя");
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Получен запрос на получение пользователя по ID");
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addAsFriend(
            @PathVariable("id") Long id,
            @PathVariable("friendId") Long friendId) {
        log.info("Получен запрос на добавления в друзья");
        userService.addAsFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFromFriends(
            @PathVariable("id") Long id,
            @PathVariable("friendId") Long friendId) {
        log.info("Получен запрос на удаление из друзей");
        userService.removeFromFriends(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Получен запрос на получение списка друзей");
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getMutualFriends(
            @PathVariable("id") Long id,
            @PathVariable("otherId") Long otherId) {
        log.info("Получен запрос на получение списках общих друзей");
        return userService.getMutualFriends(id, otherId);
    }
}