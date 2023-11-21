package ru.yandex.practicum.filmorate.storage.mem;

import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long idCounter;

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film create(Film film) {
        film.setId(++idCounter);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film update(Film film) {
        final Long id = film.getId();
        if (!films.containsKey(id)) {
            throw new DataNotFoundException(String.format("Не удалось найти фильм с id=%s.", id));
        }
        films.put(id, film);
        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        } else {
            throw new DataNotFoundException(String.format("Не удалось найти фильм с id=%s.", id));
        }
    }
}
