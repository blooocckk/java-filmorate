package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping
    public Collection<Mpa> getAll() {
        log.info("Получен запрос на получение всех MPA");
        return mpaService.getAll();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Long id) {
        log.info("Получен запрос на получение MPA по ID");
        return mpaService.getMpaById(id);
    }
}
