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
class UserControllerTest {
    private static final String PATH = "/users";

    @Autowired
    MockMvc mockMvc;

    @Test
    public void createStandardCase() throws Exception {
        mockMvc.perform(post(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(readFromFile("controller/request/user.json")))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/user.json")));
    }

    @Test
    public void createEmptyEmail() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/user-empty-email.json")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createEmailWithoutSymbolAt() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/user-email-without-symbol-at.json")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createEmptyLogin() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/user-empty-login.json")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createLoginWithWhitespaces() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/user-login-with-whitespaces.json")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createEmptyNameShouldEqualLogin() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/user-empty-name.json")))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/user-empty-name.json")));
    }

    @Test
    public void createBirthdayInFuture() throws Exception {
        mockMvc.perform(post(PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(readFromFile("controller/request/user-birthday-in-future.json")))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getAllStandardCase() throws Exception {
        createStandardCase();

        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/user-array.json")));
    }

    @Test
    public void getAll2Users() throws Exception {
        createStandardCase();
        createStandardCase();

        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/2-users-array.json")));
    }

    @Test
    public void getAllEmpty() throws Exception {
        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/empty-array.json")));
    }

    @Test
    public void getAllAfterFailedCreate() throws Exception {
        createEmptyEmail();

        mockMvc.perform(get(PATH))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/empty-array.json")));
    }

    @Test
    public void updateStandardCase() throws Exception {
        createStandardCase();

        mockMvc.perform(put(PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(readFromFile("controller/request/user-updated.json")))
                .andExpect(status().isOk())
                .andExpect(content().json(readFromFile("controller/response/user-updated.json")));
    }

    @Test
    public void updateNotExistingUser() throws Exception {
        createStandardCase();

        assertThrows(NestedServletException.class,
                () -> mockMvc.perform(
                                put(PATH)
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(readFromFile("controller/request/user-updated-wrong-id.json")))
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