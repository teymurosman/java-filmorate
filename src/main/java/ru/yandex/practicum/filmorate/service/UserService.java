package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public User addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        return user;
    }

    public User deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);

        user.getFriends().remove(friendId);
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
