package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(Long userId, Long friendId) {
        if (userStorage.getUserById(userId) == null) {
            throw new DataNotFoundException(String.format("Не удалось найти пользователя с id=%s.", userId));
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new DataNotFoundException(String.format("Не удалось найти пользователя с id=%s.", friendId));
        }

        User user = userStorage.getUserById(userId);
        user.getFriends().add(friendId);

        User friend = userStorage.getUserById(friendId);
        friend.getFriends().add(userId);

        return user;
    }

    public User deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        user.getFriends().remove(friendId);

        User friend = userStorage.getUserById(friendId);
        friend.getFriends().remove(userId);

        return user;
    }

    public List<User> getFriends(Long userId) {
        return userStorage.getUserById(userId).getFriends().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        Set<Long> friendsOfFirstUser = userStorage.getUserById(id).getFriends();
        Set<Long> friendsOfSecondUser = userStorage.getUserById(otherId).getFriends();

        return friendsOfFirstUser.stream()
                .filter(friendsOfSecondUser::contains)
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }
}
