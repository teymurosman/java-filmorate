package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Genre extends AbstractEntity<Integer> implements Comparable<Genre> {

    private String name;

    @Override
    public int compareTo(Genre o) {
        return this.getId().compareTo(o.getId());
    }
}
