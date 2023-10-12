package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class User extends AbstractEntity {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "\\S+")
    private String login;

    private String name;

    @PastOrPresent
    private LocalDate birthday;
}
