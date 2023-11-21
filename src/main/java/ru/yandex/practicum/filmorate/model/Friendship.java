package ru.yandex.practicum.filmorate.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"friend_id"})
public class Friendship {

    private Long friend_id;

    @Builder.Default
    private boolean isConfirmed = false;
}
