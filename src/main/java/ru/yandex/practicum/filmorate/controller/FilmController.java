package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController extends AbstractController<Film> {

    @GetMapping
    public List<Film> getAll() {
        log.info("Поступил запрос на получение списка всех фильмов");
        return super.getAll();
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Поступил запрос на создание фильма {}", film);
        return super.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Поступил запрос на обновление фильма {}", film);
        return super.update(film);
    }
}
