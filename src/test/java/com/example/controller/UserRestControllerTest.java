package com.example.controller;

import com.example.dto.LoginRequest;
import com.example.dto.jwt.JwtResponse;
import com.example.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserRestControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    @Value("${app.version}")
    private String path;


    @Autowired
    public UserRestControllerTest(MockMvc mockMvc, ObjectMapper mapper) {
        this.mockMvc = mockMvc;
        this.mapper = mapper;
    }

    @Nested
    @Getter
    @Setter
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("User CRUD - Successful Scenarios")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CRUDOperationsSuccess {
        private final static LoginRequest USER_DTO = new LoginRequest("alexandr", "password");
        private JwtResponse jwtResponse;
        private User user;

        @Test
        @Order(1)
        public void api_createUser() throws Exception {
            var result = mockMvc.perform(MockMvcRequestBuilders
                                                 .post("/api/v" + path + "/user/create")
                                                 .contentType(MediaType.APPLICATION_JSON)
                                                 .content(mapper.writeValueAsString(USER_DTO)))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(jsonPath("$.username").value(USER_DTO.getUsername()))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andReturn();

            var responseBody = result.getResponse().getContentAsString();
            user = mapper.readValue(responseBody, User.class);
            assertNotNull(user.getId(), "User Id not be null");
        }

        @Test
        @Order(2)
        public void api_login() throws Exception {
            var result = mockMvc.perform(MockMvcRequestBuilders
                                                 .post("/api/v" + path + "/jwt/login")
                                                 .contentType(MediaType.APPLICATION_JSON)
                                                 .content(mapper.writeValueAsString(USER_DTO)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.expiryAccessToken").isNotEmpty())
                    .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                    .andExpect(jsonPath("$.expiryRefreshToken").isNotEmpty())
                    .andReturn();

            var responseBody = result.getResponse().getContentAsString();
            jwtResponse = mapper.readValue(responseBody, JwtResponse.class);

            assertNotNull(jwtResponse.accessToken(), "Access token should not be null");
            assertNotNull(jwtResponse.expiryAccessToken(), "Expiry access token should not be null");
            assertNotNull(jwtResponse.refreshToken(), "Refresh token should not be null");
            assertNotNull(jwtResponse.expiryRefreshToken(), "Expiry refresh token should not be null");
        }

        @Test
        @Order(3)
        public void api_getUser() throws Exception {
            var result = mockMvc.perform(MockMvcRequestBuilders
                                                 .get("/api/v" + path + "/user/" + user.getId())
                                                 .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.username").isNotEmpty())
                    .andExpect(jsonPath("$.password").doesNotExist())
                    .andExpect(jsonPath("$.createDate").doesNotExist())
                    .andExpect(jsonPath("$.lastUpdateDate").doesNotExist())
                    .andReturn();
        }

    }

    @Nested
    @DisplayName("Create User - Negative Scenarios")
    class CreateUserBadOperations {

        private ResultActions createUser(String username, String password) throws Exception {
            var dto = new LoginRequest(username, password);
            String str = mapper.writeValueAsString(dto);
            return mockMvc.perform(MockMvcRequestBuilders.post(
                            "/api/v" + path + "/user/create")
                                           .contentType(MediaType.APPLICATION_JSON)
                                           .content(str));
        }

        @Test
        public void api_createUser_conflict() throws Exception {
            createUser("alexandr_2", "password")
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(jsonPath("$.username").value("alexandr_2"));

            createUser("alexandr_2", "password")
                    .andExpect(MockMvcResultMatchers.status().isConflict());
        }

        @Test
        public void api_createUser_not_valid_data() throws Exception {
            createUser("", "")
                    .andExpect(MockMvcResultMatchers.status().isBadRequest());
        }
    }


}