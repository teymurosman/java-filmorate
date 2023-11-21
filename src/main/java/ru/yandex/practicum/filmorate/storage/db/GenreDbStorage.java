package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Genre> getAll() {
        String sql = "select * from genres order by genre_id";
        return jdbcTemplate.query(sql, this::mapGenre);
    }

    @Override
    public Genre getGenreById(Integer id) {
        String sql = "select * from genres where genre_id = ?";
        return jdbcTemplate.query(sql, this::mapGenre, id).stream().findAny()
                .orElseThrow(() -> new DataNotFoundException("Не удалось найти жанр с id=" + id));
    }

    private Genre mapGenre(ResultSet rs, int rowNum) throws SQLException {
        return Genre.builder()
                .id(rs.getInt("genre_id"))
                .name(rs.getString("name"))
                .build();
    }
}
