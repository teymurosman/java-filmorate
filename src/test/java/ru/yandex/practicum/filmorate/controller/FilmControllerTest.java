package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class FilmControllerTest {
    private static final String PATH = "/films";

    @Autowired
    MockMvc mockMvc;

    @Test
    public void createStandardCase() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/film.json")))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/film.json")));
    }

    @Test
    public void createEmptyName() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/film-empty-name.json")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createTooLongDescription() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/film-too-long-description.json")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createInvalidReleaseDate() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/film-invalid-release-date.json")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createNegativeDuration() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/film-negative-duration.json")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createZeroDuration() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/film-zero-duration.json")))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllStandardCase() throws Exception {
        createStandardCase();

        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/film-array.json")));
    }

    @Test
    public void getAll2Films() throws Exception {
        createStandardCase();
        createStandardCase();

        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/2-films-array.json")));
    }

    @Test
    public void getAllEmpty() throws Exception {
        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/empty-array.json")));
    }

    @Test
    public void getAllAfterFailedCreate() throws Exception {
        createEmptyName();

        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/empty-array.json")));
    }

    @Test
    public void updateStandardCase() throws Exception {
        createStandardCase();

        mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/film-updated.json")))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/film-updated.json")));
    }

    @Test
    public void updateNotExistingFilm() throws Exception {
        createStandardCase();

        mockMvc.perform(put(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/film-updated-wrong-id.json")))
                .andExpect(status().isNotFound())
                .andExpect(content().json(readFromFile(
                        "controller/response/error-film-not-found-id-9999.json")));
    }

    @Test
    public void getFilmByIdStandardCase() throws Exception {
        createStandardCase();

        mockMvc.perform(get(PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/film.json")));
    }

    @Test
    public void getFilmByWrongId() throws Exception {
        createStandardCase();

        mockMvc.perform(get(PATH + "/9999"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(readFromFile(
                        "controller/response/error-film-not-found-id-9999.json")));
    }

    @Test
    public void addLikeStandardCase() throws Exception {
        createStandardCase();

        mockMvc.perform(put(PATH + "/1/like/1"))
                .andExpect(status().isOk());
        mockMvc.perform(get(PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/film-with-like-id-1.json")));
    }

    @Test
    public void addLikeByWrongFilmId() throws Exception {
        createStandardCase();

        mockMvc.perform(put(PATH + "/9999/like/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().json(readFromFile(
                        "controller/response/error-film-not-found-id-9999.json")));
    }

    @Test
    public void deleteLikeStandardCase() throws Exception {
        addLikeStandardCase();

        mockMvc.perform(delete(PATH + "/1/like/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/film.json")));
    }

    @Test
    public void deleteLikeByWrongFilmId() throws Exception {
        addLikeStandardCase();

        mockMvc.perform(delete(PATH + "/9999/like/1"))
                .andExpect(status().isNotFound());
        mockMvc.perform(get(PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/film-with-like-id-1.json")));
    }

    @Test
    public void getTopFilmsStandardCase() throws Exception {
        createStandardCase();
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/film-with-3-likes.json")))
                .andExpect(status().isOk());
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/film-with-like-id-1.json")))
                .andExpect(status().isOk());

        mockMvc.perform(get(PATH + "/popular"))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/films-popular-3-1-0-likes.json")));
    }

    @Test
    public void getMostLikedFilm() throws Exception {
        getTopFilmsStandardCase();

        mockMvc.perform(get(PATH + "/popular?count=1"))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/film-with-3-likes-array.json")));
    }

    @Test
    public void getTopFilmsAssertDefaultSizeOfParam() throws Exception {
        for (int i = 0; i < 15; i++) {
            createStandardCase(); // Создаем 15 фильмов
        }

        String result = mockMvc.perform(get(PATH + "/popular")) // count default = 10
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString(StandardCharsets.UTF_8);
        assertEquals(10, StringUtils.countOccurrencesOf(result, "id")); // Сколько раз встречается "id"
    }

    private String readFromFile(String filename) {
        try {
            return Files.readString(ResourceUtils.getFile("classpath:" + filename).toPath(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            return "";
        }
    }
}