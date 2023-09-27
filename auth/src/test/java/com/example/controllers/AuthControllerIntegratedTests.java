package com.example.controllers;

import com.example.dto.TokenResponseDTO;
import com.example.models.User;
import com.example.utils.JsonUtils;
import com.example.utils.PasswordUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@PropertySource("classpath:application-test.properties")
class AuthControllerIntegratedTests {

    private static final Logger LOG = LoggerFactory.getLogger(AuthControllerIntegratedTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordUtils passwordUtils;

    /* Pre-generated password for the user */
    private String preEncodedPassword = "eyJhbGciOiJIUzUxMiJ9.eyJST0xFUyI6W3siYXV0aG9yaXR5IjoiUk9MRV9VU0VSIn1dLCJzdWIiO" +
            "iJ0ZXN0QG1haWwuY29tIiwiaWF0IjoxNjk1Nzc5MTUzLCJleHAiOjE2OTU3ODAwNTN9.Ng2maH_0gAS6IJVOmwgbPkV_N7FQE1M6T8XSRZu" +
            "FbNE5f49M9JIaDyNjUpdQV65_Y2jnZE2f232eEI5HuUTOBg";

    @Test
    void registerTest() throws Exception {
        User testUser = getTestUser();
        //Mockito.when(authService.registerUser(any())).thenReturn(testAccessToken);

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJsonString(testUser)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        TokenResponseDTO responseDTO = (TokenResponseDTO) JsonUtils.asObject(responseString, TokenResponseDTO.class);
        LOG.info("responseDTO = {}", responseDTO.toString());
        //Assertions.assertEquals(responseDTO.getAccessToken(), testAccessToken);
        boolean validatePassword = passwordUtils.validateLoginCredentials(testUser.getPassword(), preEncodedPassword);
        Assertions.assertTrue(validatePassword);
        Assertions.assertEquals(responseDTO.getEmail(), testUser.getEmail());
    }

    //@Test
    void loginTest() throws Exception {
        User testUser = getTestUser();
        String testAccessToken = "accessToken";
        //Mockito.when(authService.loginUser(any())).thenReturn(testAccessToken);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(JsonUtils.asJsonString(testUser)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = result.getResponse().getContentAsString();
        TokenResponseDTO responseDTO = (TokenResponseDTO) JsonUtils.asObject(responseString, TokenResponseDTO.class);
        Assertions.assertEquals(responseDTO.getAccessToken(), testAccessToken);
        Assertions.assertEquals(responseDTO.getEmail(), testUser.getEmail());
    }

    private User getTestUser() {
        return new User("test@mail.com", "123");
    }

}