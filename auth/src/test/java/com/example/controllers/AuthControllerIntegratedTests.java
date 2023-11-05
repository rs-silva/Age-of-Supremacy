package com.example.controllers;

import com.example.dto.LoginResponseDTO;
import com.example.dto.RefreshTokenResponseDTO;
import com.example.exceptions.ErrorMessage;
import com.example.models.RefreshToken;
import com.example.models.User;
import com.example.repositories.RefreshTokenRepository;
import com.example.repositories.UserRepository;
import com.example.utils.AuthConstants;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@PropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AuthControllerIntegratedTests {

    private static final Logger LOG = LoggerFactory.getLogger(AuthControllerIntegratedTests.class);

    private final String CLASS_NAME = "AuthControllerIntegratedTests";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordUtils passwordUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private static String refreshToken;

    private static String accessToken;

    private static UUID userId;

    @Test
    @Order(0)
    void registerTest() throws Exception {
        LOG.info(CLASS_NAME + "::registerTest");
        User testUser = getTestUser();

        String result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJsonString(testUser)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        LoginResponseDTO responseDTO = (LoginResponseDTO) JsonUtils.asObject(result, LoginResponseDTO.class);
        LOG.info("responseDTO = {}", responseDTO.toString());

        /* Pre-generated encoded password for the user (password: 123) */
        String preEncodedPassword = "$2a$10$ifVMES9Y3Mrd5e96KPF2VuZIlh6cRJbHQY/GEflRx/KJV2PKqXwae";
        boolean validatePassword = passwordUtils.validateLoginPassword(testUser.getPassword(), preEncodedPassword);

        Assertions.assertTrue(validatePassword);
        Assertions.assertEquals(responseDTO.getEmail(), testUser.getEmail());
        Assertions.assertNotNull(responseDTO.getUserId());
        Assertions.assertNotNull(responseDTO.getRefreshToken());
        Assertions.assertNotNull(responseDTO.getAccessToken());

        /* Confirm that the user was successfully created */
        Assertions.assertNotNull(userRepository.findByEmail(testUser.getEmail()));
    }

    @Test
    @Order(1)
    void loginTest() throws Exception {
        LOG.info(CLASS_NAME + "::loginTest");
        User testUser = getTestUser();

        String result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJsonString(testUser)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        LoginResponseDTO responseDTO = (LoginResponseDTO) JsonUtils.asObject(result, LoginResponseDTO.class);
        Assertions.assertEquals(responseDTO.getEmail(), testUser.getEmail());
        Assertions.assertNotNull(responseDTO.getUserId());
        Assertions.assertNotNull(responseDTO.getRefreshToken());
        Assertions.assertNotNull(responseDTO.getAccessToken());

        /* Set Refresh Token */
        refreshToken = responseDTO.getRefreshToken();
        /* Set Access Token */
        accessToken = responseDTO.getAccessToken();
        /* Set userId */
        userId = responseDTO.getUserId();
    }

    @Test
    @Order(2)
    void attemptToRegisterUserThatAlreadyExists() throws Exception {
        LOG.info(CLASS_NAME + "::attemptToRegisterUserThatAlreadyExists");
        User testUser = getTestUser();

        String result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJsonString(testUser)))
                .andExpect(status().isConflict())
                .andReturn().getResponse().getContentAsString();

        ErrorMessage errorMessage = (ErrorMessage) JsonUtils.asObject(result, ErrorMessage.class);
        Assertions.assertEquals(String.format(AuthConstants.USER_ALREADY_EXISTS_EXCEPTION, testUser.getEmail()), errorMessage.getMessage());
    }

    @Test
    @Order(3)
    void attemptToLoginUserThatDoesNotExist() throws Exception {
        LOG.info(CLASS_NAME + "::attemptToLoginUserThatDoesNotExist");
        String unknownEmail = "unknown@mail.com";
        User testUser = new User(unknownEmail, "123");

        String result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJsonString(testUser)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        ErrorMessage errorMessage = (ErrorMessage) JsonUtils.asObject(result, ErrorMessage.class);
        Assertions.assertEquals(String.format(AuthConstants.USER_WITH_EMAIL_NOT_FOUND_EXCEPTION, unknownEmail), errorMessage.getMessage());
    }

    @Test
    @Order(4)
    void attemptToLoginUserUsingWrongCredentials() throws Exception {
        LOG.info(CLASS_NAME + "::attemptToLoginUserUsingWrongCredentials");
        User testUser = new User("test@mail.com", "1234");

        String result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJsonString(testUser)))
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        ErrorMessage errorMessage = (ErrorMessage) JsonUtils.asObject(result, ErrorMessage.class);
        Assertions.assertEquals(AuthConstants.WRONG_LOGIN_CREDENTIALS, errorMessage.getMessage());
    }

    @Test
    @Order(5)
    void refreshToken() throws Exception {
        LOG.info(CLASS_NAME + "::refreshToken");
        User testUser = getTestUser();

        String result = mockMvc.perform(get("/api/auth/refreshToken")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .param("userEmail", testUser.getEmail())
                        .param("refreshToken", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        RefreshTokenResponseDTO responseDTO = (RefreshTokenResponseDTO) JsonUtils.asObject(result, RefreshTokenResponseDTO.class);
        Assertions.assertNotNull(responseDTO.getAccessToken());

        /* Set Access Token */
        accessToken = responseDTO.getAccessToken();
    }

    @Test
    @Order(6)
    void refreshTokenUsingExpiredRefreshToken() throws Exception {
        LOG.info(CLASS_NAME + "::refreshTokenUsingExpiredRefreshToken");
        User testUser = getTestUser();

        /* Force expire current Refresh Token for this user */
        RefreshToken refreshTokenToExpire = refreshTokenRepository.findByToken(refreshToken);
        Date date = new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime();
        refreshTokenToExpire.setExpiryDate(date);
        refreshTokenRepository.save(refreshTokenToExpire);

        String result = mockMvc.perform(get("/api/auth/refreshToken")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .param("userEmail", testUser.getEmail())
                        .param("refreshToken", refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();

        ErrorMessage errorMessage = (ErrorMessage) JsonUtils.asObject(result, ErrorMessage.class);
        Assertions.assertEquals(AuthConstants.REFRESH_TOKEN_EXPIRED, errorMessage.getMessage());
    }

    @Test
    @Order(7)
    void refreshTokenUsingInvalidRefreshToken() throws Exception {
        LOG.info(CLASS_NAME + "::refreshTokenUsingInvalidRefreshToken");
        User testUser = getTestUser();
        UUID invalidRefreshToken = UUID.randomUUID();

        String result = mockMvc.perform(get("/api/auth/refreshToken")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .param("userEmail", testUser.getEmail())
                        .param("refreshToken", String.valueOf(invalidRefreshToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();

        ErrorMessage errorMessage = (ErrorMessage) JsonUtils.asObject(result, ErrorMessage.class);
        Assertions.assertEquals(AuthConstants.REFRESH_TOKEN_NOT_FOUND, errorMessage.getMessage());
    }

    @Test
    @Order(8)
    void updateUser() throws Exception {
        LOG.info(CLASS_NAME + "::updateUser");
        User testUser = getTestUser();
        User updatedUser = new User("test2@mail.com", "1234");

        String result = mockMvc.perform(put("/api/user/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .param("currentUserEmail", testUser.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJsonString(updatedUser)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        LoginResponseDTO responseDTO = (LoginResponseDTO) JsonUtils.asObject(result, LoginResponseDTO.class);
        Assertions.assertEquals(responseDTO.getEmail(), updatedUser.getEmail());
        Assertions.assertNotNull(responseDTO.getUserId());
        Assertions.assertNotNull(responseDTO.getRefreshToken());
        Assertions.assertNotNull(responseDTO.getAccessToken());

        /* Set Access Token */
        accessToken = responseDTO.getAccessToken();
    }

    @Test
    @Order(9)
    void updateUserUsingInvalidId() throws Exception {
        LOG.info(CLASS_NAME + "::updateUserUsingInvalidId");
        User testUser = getTestUser();
        User updatedUser = new User("test2@mail.com", "1234");
        UUID invalidUUID = UUID.randomUUID();

        String result = mockMvc.perform(put("/api/user/" + invalidUUID)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .param("currentUserEmail", testUser.getEmail())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJsonString(updatedUser)))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        ErrorMessage errorMessage = (ErrorMessage) JsonUtils.asObject(result, ErrorMessage.class);
        Assertions.assertEquals(String.format(AuthConstants.USER_WITH_ID_NOT_FOUND_EXCEPTION, invalidUUID), errorMessage.getMessage());
    }

    @Test
    @Order(10)
    void updateUserUsingInvalidEmailInRequestParameter() throws Exception {
        LOG.info(CLASS_NAME + "::updateUserUsingInvalidEmailInRequestParameter");
        User updatedUser = new User("test2@mail.com", "1234");

        String response = mockMvc.perform(put("/api/user/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .param("currentUserEmail", "wrongEmail@mail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJsonString(updatedUser)))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();

        ErrorMessage responseDTO = (ErrorMessage) JsonUtils.asObject(response, ErrorMessage.class);
        Assertions.assertEquals(responseDTO.getMessage(), AuthConstants.USER_EMAIL_FROM_GIVEN_ID_DOES_NOT_MATCH_EMAIL_FROM_REQUEST_PARAM);
    }

    @Test
    @Order(11)
    void updateUserUsingTokenWithDifferentEmail() throws Exception {
        LOG.info(CLASS_NAME + "::updateUserUsingTokenWithDifferentEmail");
        String invalidAccessToken = "eyJhbGciOiJIUzUxMiJ9.eyJST0xFUyI6W3siYXV0aG9yaXR5IjoiUk9MRV9VU0VSIn1dLCJzdWIiOiJ1c2VyQG1haWwuY29tIiwiaWF0IjoxNjk2ODA3ODM3LCJleHAiOjkyMjMzNzIwMzY4NTQ3NzV9.RFgN8mxlhwHTHDMlvGfyTi-U0H7p7V8aesZG_xhpUt95C0MERWhk_fmLNV528T3vsgMHhydPl4hPqsGnhfMDeg";
        User updatedUser = new User("test2@mail.com", "1234");

        String response = mockMvc.perform(put("/api/user/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + invalidAccessToken)
                        .param("currentUserEmail", "test2@mail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJsonString(updatedUser)))
                .andExpect(status().isForbidden())
                .andReturn().getResponse().getContentAsString();

        ErrorMessage responseDTO = (ErrorMessage) JsonUtils.asObject(response, ErrorMessage.class);
        Assertions.assertEquals(responseDTO.getMessage(), AuthConstants.CUSTOMER_EMAIL_DIFFERENT_FROM_TOKEN_EMAIL);
    }

    @Test
    @Order(12)
    void deleteUser() throws Exception {
        LOG.info(CLASS_NAME + "::deleteUser");

        mockMvc.perform(delete("/api/user/" + userId.toString())
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .param("userEmail", "test2@mail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        /* Confirm that the user was successfully deleted */
        Assertions.assertNull(userRepository.findByEmail("test2@mail.com"));
    }

    @Test
    @Order(13)
    void deleteUserUsingInvalidId() throws Exception {
        LOG.info(CLASS_NAME + "::deleteUserUsingInvalidId");

        String result = mockMvc.perform(delete("/api/user/" + userId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .param("userEmail", "test2@mail.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn().getResponse().getContentAsString();

        ErrorMessage errorMessage = (ErrorMessage) JsonUtils.asObject(result, ErrorMessage.class);
        Assertions.assertEquals(String.format(AuthConstants.USER_WITH_ID_NOT_FOUND_EXCEPTION, userId), errorMessage.getMessage());
    }

    @WithMockUser(authorities = "ROLE_ADMIN")
    @Test
    @Order(14)
    void findAllUsers() throws Exception {
        /* Add user to database to test */
        userRepository.save(getTestUser());

        LOG.info(CLASS_NAME + "::findAllUsers");

        String response = mockMvc.perform(get("/api/user/findAll")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<User> usersList = (List<User>)(Object) JsonUtils.asObjectList(response, User.class);
        Assertions.assertEquals(1, usersList.size());
        Assertions.assertEquals(getTestUser().getEmail(), usersList.get(0).getEmail());
    }

    @Test
    @Order(15)
    void findAllUsersWithoutAdminRole() throws Exception {
        LOG.info(CLASS_NAME + "::findAllUsersWithoutAdminRole");

        mockMvc.perform(get("/api/user/findAll")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private User getTestUser() {
        return new User("test@mail.com", "123");
    }

}