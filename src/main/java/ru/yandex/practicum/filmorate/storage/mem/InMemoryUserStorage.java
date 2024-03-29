package ru.yandex.practicum.filmorate.storage.mem;

import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User create(User user) {
        user.setId(++idCounter);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        final Long id = user.getId();
        if (!users.containsKey(id)) {
            throw new DataNotFoundException(String.format("Не удалось найти пользователя с id=%s.", id));
        }
        users.put(id, user);
        return user;
    }

    @Override
    public User getUserById(Long id) {
        if (users.containsKey(id)) {
            return users.get(id);
        } else {
            throw new DataNotFoundException(String.format("Не удалось найти пользователя с id=%s.", id));
        }
    }

}
