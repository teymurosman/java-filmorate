package ru.yandex.practicum.filmorate.controller;

import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.AbstractEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractController<T extends AbstractEntity> {
    private final Map<Long, T> storage = new HashMap<>();
    private long idCounter;

    public List<T> getAll() {
        return List.copyOf(storage.values());
    }
    public T create(T data) {
        data.setId(++idCounter);
        storage.put(data.getId(), data);
        return data;
    }

    public T update(T data) {
        if (!storage.containsKey(data.getId())) {
            throw new DataNotFoundException("Не удалось найти элемент для обновления");
        }
        storage.put(data.getId(), data);
        return data;
    }

}
