package com.example.controllers;

import com.example.dto.TokenResponseDTO;
import com.example.models.User;
import com.example.repositories.UserRepository;
import com.example.utils.JsonUtils;
import com.example.utils.PasswordUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@PropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthControllerIntegratedTests {

    private static final Logger LOG = LoggerFactory.getLogger(AuthControllerIntegratedTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private UserRepository userRepository;

    /* Pre-generated encoded password for the user */
    private String preEncodedPassword = "$2a$10$ifVMES9Y3Mrd5e96KPF2VuZIlh6cRJbHQY/GEflRx/KJV2PKqXwae";

    @Test
    @Order(0)
    void registerTest() throws Exception {
        User testUser = getTestUser();
        /* Confirm that the user doesn't already exist */
        Assertions.assertNull(userRepository.findByEmail(testUser.getEmail()));

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJsonString(testUser)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        TokenResponseDTO responseDTO = (TokenResponseDTO) JsonUtils.asObject(responseString, TokenResponseDTO.class);
        LOG.info("responseDTO = {}", responseDTO.toString());
        boolean validatePassword = passwordUtils.validateLoginPassword(testUser.getPassword(), preEncodedPassword);
        Assertions.assertTrue(validatePassword);
        Assertions.assertEquals(responseDTO.getEmail(), testUser.getEmail());
        Assertions.assertNotNull(responseDTO.getAccessToken());
        /* Confirm that the user was successfully created */
        Assertions.assertNotNull(userRepository.findByEmail(testUser.getEmail()));
    }

    @Test
    @Order(1)
    void loginTest() throws Exception {
        User testUser = getTestUser();

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJsonString(testUser)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        TokenResponseDTO responseDTO = (TokenResponseDTO) JsonUtils.asObject(responseString, TokenResponseDTO.class);
        Assertions.assertEquals(responseDTO.getEmail(), testUser.getEmail());
        Assertions.assertNotNull(responseDTO.getAccessToken());
    }

    @Test
    @Order(2)
    void attemptToRegisterUserThatAlreadyExists() throws Exception {
        User testUser = getTestUser();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJsonString(testUser)))
                .andExpect(status().isConflict());
    }

    @Test
    @Order(3)
    void attemptToLoginUserThatDoesNotExist() throws Exception {
        User testUser = new User("unknown@mail.com", "123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJsonString(testUser)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(4)
    void attemptToLoginUserUsingWrongCredentials() throws Exception {
        User testUser = new User("test@mail.com", "1234");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJsonString(testUser)))
                .andExpect(status().isUnauthorized());
    }

    private User getTestUser() {
        return new User("test@mail.com", "123");
    }

}