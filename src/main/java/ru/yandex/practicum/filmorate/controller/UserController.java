package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Поступил запрос на получение списка всех пользователей.");
        return userService.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Поступил запрос на добавление пользователя {}.", user);
        return userService.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Поступил запрос на обновление пользователя {}.", user);
        return userService.update(user);
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        log.info("Поступил запрос на получение пользователя по id = {}.", id);
        return userService.getUserById(id);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public User addFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Поступил запрос от userId={} на добавление друга friendId={}.", id, friendId);
        return userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public User deleteFriend(@PathVariable Long id, @PathVariable Long friendId) {
        log.info("Поступил запрос от userId={} на удаление друга friendId={}.", id, friendId);
        return userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable Long id) {
        log.info("Поступил запрос на получение списка друзей пользователя с id={}.", id);
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable Long id, @PathVariable Long otherId) {
        log.info("Поступил запрос на получение списка общих друзей id={} и otherId={}.", id, otherId);
        return userService.getCommonFriends(id, otherId);
    }
}
