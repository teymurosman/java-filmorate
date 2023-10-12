package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.util.ResourceUtils;
import org.springframework.web.util.NestedServletException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;


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
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createTooLongDescription() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/film-too-long-description.json")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createInvalidReleaseDate() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/film-invalid-release-date.json")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createNegativeDuration() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/film-negative-duration.json")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createZeroDuration() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/film-zero-duration.json")))
                .andExpect(status().is4xxClientError());
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

        assertThrows(NestedServletException.class,
                () -> mockMvc.perform(
                                put(PATH)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(readFromFile("controller/request/film-updated-wrong-id.json")))
                        .andExpect(status().is5xxServerError()));
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