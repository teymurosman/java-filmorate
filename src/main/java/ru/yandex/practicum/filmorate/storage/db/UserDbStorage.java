package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Component("userDbStorage")
@Primary
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getAll() {
        String sql = "select * from users";
        return jdbcTemplate.query(sql, this::mapUser);
    }

    @Override
    public User getUserById(Long id) {
        String sql = "select * from users where user_id = ?";
        return jdbcTemplate.query(sql, this::mapUser, id).stream().findAny()
                .orElseThrow(() -> new DataNotFoundException("Не удалось найти пользователя с id=" + id));
    }

    @Override
    public User create(User user) {
        String sql = "insert into users (email, login, name, birthday) values (?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sql, new String[]{"user_id"});
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setString(4, String.valueOf(user.getBirthday()));
            return ps;
        }, keyHolder);

        user.setId(keyHolder.getKey().longValue());

        return user;
    }

    @Override
    public User update(User user) {
        final long userId = user.getId();
        String sql = "update users " +
                "set email = ?, login = ?, name = ?, birthday = ? " +
                "where user_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                userId);

        if (rowsUpdated == 0) {
            throw new DataNotFoundException("Не удалось найти пользователя с id=" + userId);
        }

        Set<Friendship> friendshipsBeforeUpdate = new HashSet<>(getFriendsByUserId(userId));
        Set<Friendship> newFriendships = user.getFriends();

//        Delete
        Set<Friendship> friendshipsToDelete = new HashSet<>(friendshipsBeforeUpdate);
        friendshipsToDelete.removeAll(newFriendships);
        deleteFriendships(userId, friendshipsToDelete);

//        Update
        Map<Long, Boolean> statusesBeforeUpdate = friendshipsBeforeUpdate.stream()
                        .collect(Collectors.toMap(Friendship::getFriendId, Friendship::isConfirmed));
        Set<Friendship> friendshipsToUpdate = newFriendships.stream()
                .filter(friendshipsBeforeUpdate::contains)
                .filter(f -> f.isConfirmed() != statusesBeforeUpdate.get(f.getFriendId()))
                .collect(Collectors.toSet());
        updateFriendshipsStatuses(userId, friendshipsToUpdate);

//        Add
        Set<Friendship> friendshipsToAdd = new HashSet<>(newFriendships);
        friendshipsToAdd.removeAll(friendshipsBeforeUpdate);
        addFriendships(userId, friendshipsToAdd);

        return user;
    }

    private User mapUser(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .login(rs.getString("login"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .build();

        user.getFriends().addAll(getFriendsByUserId(user.getId()));
        return user;
    }

    private Friendship mapFriendship(ResultSet rs, int rowNum) throws SQLException {
        return Friendship.builder()
                .friendId(rs.getLong("friend_id"))
                .isConfirmed(rs.getBoolean("is_confirmed"))
                .build();
    }

    private List<Friendship> getFriendsByUserId(Long userId) {
        String sql = "select friend_id, is_confirmed from friendships where user_id = ?";
        return jdbcTemplate.query(sql, this::mapFriendship, userId);
    }

    private void addFriendships(Long userId, Set<Friendship> friendships) {
        String sql = "insert into friendships (user_id, friend_id, is_confirmed) values (?, ?, ?)";
        for (Friendship friendship : friendships) {
            jdbcTemplate.update(sql, userId, friendship.getFriendId(), friendship.isConfirmed());
        }
    }

    private void deleteFriendships(Long userId, Set<Friendship> friendships) {
        String sql = "delete from friendships where user_id = ? and friend_id = ?";
        for (Friendship friendship : friendships) {
            jdbcTemplate.update(sql, userId, friendship.getFriendId());
        }
    }

    private void updateFriendshipsStatuses(Long userId, Set<Friendship> friendships) {
        String sql = "update friendships set is_confirmed = ? where user_id ? and friend_id = ?";
        for (Friendship friendship : friendships) {
            jdbcTemplate.update(sql, friendship.isConfirmed(), userId, friendship.getFriendId());
        }
    }
}
