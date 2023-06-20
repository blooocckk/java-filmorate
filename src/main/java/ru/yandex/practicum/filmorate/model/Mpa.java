package ru.yandex.practicum.filmorate.model;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class Mpa extends AbstractModel {
    private String name;
}