package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController extends AbstractController<User> {

    @GetMapping
    public List<User> getAll() {
        log.info("Поступил запрос на получение списка всех пользователей");
        return super.getAll();
    }

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        log.info("Поступил запрос на добавление пользователя {}", user);
        validateName(user);
        return super.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user) {
        log.info("Поступил запрос на обновление пользователя {}", user);
        validateName(user);
        return super.update(user);
    }

    private void validateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}
