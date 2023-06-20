package ru.yandex.practicum.filmorate.model;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
public class Genre extends AbstractModel {
    private String name;
}