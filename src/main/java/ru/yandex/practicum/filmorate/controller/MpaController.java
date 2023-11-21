package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
@RequiredArgsConstructor
public class MpaController {

    private final MpaService mpaService;

    @GetMapping
    public List<Mpa> getAll() {
        log.info("Поступил запрос на получение списка всех MPA рейтингов.");
        return mpaService.getAll();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Integer id) {
        log.info("Поступил запрос на получение MPA рейтинга с id={}", id);
        return mpaService.getMpaById(id);
    }
}