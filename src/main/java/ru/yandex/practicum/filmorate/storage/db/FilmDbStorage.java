package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DataNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.GenreStorage;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Component("filmDbStorage")
@Primary
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;

    @Override
    public List<Film> getAll() {
        String sql = "select * from films";
        return jdbcTemplate.query(sql, this::mapFilm);
    }

    @Override
    public Film create(Film film) {
        String sql = "insert into films (name, description, release_date, duration, mpa_id) " +
                "values (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(sql, new String[]{"film_id"});
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setString(3, String.valueOf(film.getReleaseDate()));
            ps.setString(4, String.valueOf(film.getDuration()));
            ps.setString(5, String.valueOf(film.getMpa().getId()));
            return ps;
        }, keyHolder);

        film.setId(keyHolder.getKey().longValue());

        addFilmGenres(film.getId(), film.getGenres());
        return film;
    }

    @Override
    public Film update(Film film) {
        final long filmId = film.getId();
        String sql = "update films " +
                "set name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ? " +
                "where film_id = ?";
        int rowsUpdated = jdbcTemplate.update(sql, // Проверка количества измененных записей
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                filmId);

        if (rowsUpdated == 0) {
            throw new DataNotFoundException("Не удалось найти фильм с id=" + filmId);
        }

        Set<Genre> genresBeforeUpdate = getGenresByFilmId(filmId);

        Set<Genre> genresToDelete = new HashSet<>(genresBeforeUpdate);
        genresToDelete.removeAll(film.getGenres());
        deleteFilmGenres(filmId, genresToDelete);

        Set<Genre> genresToAdd = new HashSet<>(film.getGenres());
        genresToAdd.removeAll(genresBeforeUpdate);
        addFilmGenres(filmId, genresToAdd);

        film.getGenres().forEach(genre -> genre.setName(genreStorage.getGenreById(genre.getId()).getName()));

        Set<Long> likesBeforeUpdate = new HashSet<>(getLikesByFilmId(filmId));

        Set<Long> likesToDelete = new HashSet<>(likesBeforeUpdate);
        likesToDelete.removeAll(film.getLikes());
        deleteLikes(filmId, likesToDelete);

        Set<Long> likesToAdd = new HashSet<>(film.getLikes());
        likesToAdd.removeAll(likesBeforeUpdate);
        addLikes(filmId, likesToAdd);

//      В тестах требуются отсортированные жанры при обновлении фильма, а у данного объекта hashset вместо treeset
        film.setGenres(new TreeSet<>(film.getGenres()));

        return film;
    }

    @Override
    public Film getFilmById(Long id) {
        String sql = "select * from films where film_id = ?";
        return jdbcTemplate.query(sql, this::mapFilm, id).stream().findAny()
                .orElseThrow(() -> new DataNotFoundException("Не удалось найти фильм с id=" + id));
    }

    private Film mapFilm(ResultSet rs, int rowNum) throws SQLException {

        Film film = Film.builder()
                .id(rs.getLong("film_id"))
                .name(rs.getString("name"))
                .description(rs.getString("description"))
                .releaseDate(rs.getDate("release_date").toLocalDate())
                .duration(rs.getInt("duration"))
                .mpa(mpaStorage.getMpaById(rs.getInt("mpa_id")))
                .build();

        film.getGenres().addAll(getGenresByFilmId(film.getId()));
        film.getLikes().addAll(getLikesByFilmId(film.getId()));

        return film;
    }

    private Set<Genre> getGenresByFilmId(Long filmId) {
        String sql = "select genre_id from film_genre where film_id = ?";
        List<Integer> genreIds = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("genre_id"), filmId);

        return  genreIds.stream()
                .map(genreStorage::getGenreById)
                .collect(Collectors.toSet());
    }

    private List<Long> getLikesByFilmId(Long filmId) {
        String sql = "select user_id from likes where film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("user_id"), filmId);
    }

    private void addFilmGenres(Long filmId, Set<Genre> genres) {
        String sql = "insert into film_genre (genre_id, film_id) values (?, ?)";
        for (Genre genre : genres) {
            jdbcTemplate.update(sql, genre.getId(), filmId);
        }
    }

    private void deleteFilmGenres(Long filmId, Set<Genre> genres) {
        String sql = "delete from film_genre where genre_id = ? and film_id = ?";
        for (Genre genre : genres) {
            jdbcTemplate.update(sql, genre.getId(), filmId);
        }
    }

    private void addLikes(Long filmId, Set<Long> likes) {
        String sql = "insert into likes (film_id, user_id) values (?, ?)";
        for (Long userId : likes) {
            jdbcTemplate.update(sql, filmId, userId);
        }
    }

    private void deleteLikes(Long filmId, Set<Long> likes) {
        String sql = "delete from likes where film_id = ? and user_id = ?";
        for (Long userId : likes) {
            jdbcTemplate.update(sql, filmId, userId);
        }
    }
}
