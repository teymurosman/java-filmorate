package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    @Qualifier("userDbStorage")
    private final UserStorage userStorage;

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User create(User user) {
        validateName(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validateName(user);
        return userStorage.update(user);
    }

    public User getUserById(Long id) {
        return userStorage.getUserById(id);
    }

    public User addFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        userStorage.getUserById(friendId);

        user.getFriends().add(Friendship.builder().friend_id(friendId).build());
        userStorage.update(user);

        return user;
    }

    public User deleteFriend(Long userId, Long friendId) {
        User user = userStorage.getUserById(userId);
        userStorage.getUserById(friendId);

        user.getFriends().remove(Friendship.builder().friend_id(friendId).build());
        userStorage.update(user);

        return user;
    }

    public List<User> getFriends(Long userId) {
        return userStorage.getUserById(userId).getFriends().stream()
                .map(f -> userStorage.getUserById(f.getFriend_id()))
                .collect(Collectors.toList());
    }

    public List<User> getCommonFriends(Long id, Long otherId) {
        Set<Friendship> friendsOfFirstUser = userStorage.getUserById(id).getFriends();
        Set<Friendship> friendsOfSecondUser = userStorage.getUserById(otherId).getFriends();

        return friendsOfFirstUser.stream()
                .filter(friendsOfSecondUser::contains)
                .map(f -> userStorage.getUserById(f.getFriend_id()))
                .collect(Collectors.toList());
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
