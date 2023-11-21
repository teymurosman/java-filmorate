package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> getAll() {
        String sql = "select * from mpa_rating order by mpa_id";
        return jdbcTemplate.query(sql, this::mapMpa);
    }

    @Override
    public Mpa getMpaById(Integer id) {
        String sql = "select * from mpa_rating where mpa_id = ?";
        return jdbcTemplate.query(sql, this::mapMpa, id).stream().findAny()
                .orElseThrow(() -> new DataNotFoundException("Не удалось найти рейтинг с id=" + id));
    }

    private Mpa mapMpa(ResultSet rs, int rowNum) throws SQLException {
        Integer id = rs.getInt("mpa_id");
        String name = rs.getString("name");

        return Mpa.builder()
                .id(id)
                .name(name)
                .build();
    }
}
