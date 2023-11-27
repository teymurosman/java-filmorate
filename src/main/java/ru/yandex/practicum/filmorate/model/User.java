package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class User extends AbstractEntity<Long> {

    @Email(regexp = ".+[@].+[\\.].+", message = "email должен соответствовать формату xxx@xx.x")
    private String email;

    @NotBlank(message = "логин не может быть пустым или содержать пробелы")
    @Pattern(regexp = "\\S+", message = "логин не может быть пустым или содержать пробелы")
    private String login;

    private String name;

    @PastOrPresent(message = "дата рождения не может быть в будущем")
    private LocalDate birthday;

    private final Set<Friendship> friends = new HashSet<>();
}
