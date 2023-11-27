package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"friendId"})
public class Friendship {

    private Long friendId;

    @Builder.Default
    private boolean isConfirmed = false;
}
