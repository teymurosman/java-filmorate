package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film addLike(Long filmId, Long userId) {
        final Film film = filmStorage.getFilmById(filmId);
        if (film == null) {
            throw new DataNotFoundException(String.format("Не удалось найти фильм с id=%s.", filmId));
        } else {
            film.getLikes().add(userId);
            return film;
        }
    }

    public Film deleteLike(Long filmId, Long userId) {
        final Film film = filmStorage.getFilmById(filmId);

        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
            return film;
        } else {
            throw new DataNotFoundException(String.format("Пользователь с id=%s не ставил лайк фильму с id=%s.", userId,
                    filmId));
        }
    }

    public List<Film> getTopFilms(int count) {
        return filmStorage.getAll().stream()
                .sorted((f1, f2) -> f2.getLikes().size() - f1.getLikes().size())
                .limit(count)
                .collect(Collectors.toList());
    }
}
