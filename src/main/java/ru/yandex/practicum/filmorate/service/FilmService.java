package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FilmService {

    @Qualifier("filmDbStorage")
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public Film getFilmById(Long id) {
        return filmStorage.getFilmById(id);
    }

    public Film addLike(Long filmId, Long userId) {
        final Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId); // Валидация существования пользователя

        film.getLikes().add(userId);
        filmStorage.update(film);

        return film;
    }

    public Film deleteLike(Long filmId, Long userId) {
        final Film film = filmStorage.getFilmById(filmId);
        userStorage.getUserById(userId);  // Валидация существования пользователя

        if (film.getLikes().contains(userId)) {
            film.getLikes().remove(userId);
            filmStorage.update(film);
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
