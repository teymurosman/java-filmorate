package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.yandex.practicum.filmorate.validation.FilmReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
public class Film extends AbstractEntity<Long> {

    @NotBlank(message = "название фильма не может быть пустым")
    private String name;

    @Size(min = 1, max = 200, message = "максимальная длина описания — 200 символов")
    private String description;

    @FilmReleaseDate(message = "дата релиза — не раньше 28.12.1895")
    private LocalDate releaseDate;

    @Positive(message = "продолжительность фильма должна быть положительной")
    private int duration;

    @NotNull(message = "рейтинг MPA не может быть пустым")
    Mpa mpa;

    private final Set<Long> likes = new HashSet<>();

    @Builder.Default
    private Set<Genre> genres = new TreeSet<>();
}
