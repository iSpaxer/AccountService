package com.example.controller;

import com.example.dto.LoginRequest;
import com.example.dto.UserDto;
import com.example.dto.jwt.JwtResponse;
import com.example.entity.User;
import com.example.util.ApplicationDataComponent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserRestControllerTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final ApplicationDataComponent appComponent;


    @Autowired
    public UserRestControllerTest(MockMvc mockMvc, ObjectMapper objectMapper,
                                  ApplicationDataComponent appComponent) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.appComponent = appComponent;
    }

    @Nested
    @Getter
    @Setter
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("User CRUD - Successful Scenarios")
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    class CRUDOperationsSuccess {
        private final static LoginRequest USER_LOGIN_DTO = new LoginRequest("alexandr", "password");
        private JwtResponse credential;
        private JwtResponse credentialSecondLogin;
        private User user;

        @Test
        @Order(1)
        public void api_createUser_isOk() throws Exception {
            var result = mockMvc.perform(MockMvcRequestBuilders
                                                 .post(appComponent.glueEndpoint("/user/create"))
                                                 .contentType(MediaType.APPLICATION_JSON)
                                                 .content(objectMapper.writeValueAsString(USER_LOGIN_DTO)))
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(jsonPath("$.username").value(USER_LOGIN_DTO.getUsername()))
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andReturn();

            var responseBody = result.getResponse().getContentAsString();
            user = objectMapper.readValue(responseBody, User.class);
            assertNotNull(user.getId(), "User Id not be null");
        }

        @Test
        @Order(2)
        public void api_login_isOk() throws Exception {
            credential = login_isOk();
        }

        private JwtResponse login_isOk() throws Exception {
            var result = mockMvc.perform(MockMvcRequestBuilders
                                                 .post(appComponent.glueEndpoint("/jwt/login"))
                                                 .contentType(MediaType.APPLICATION_JSON)
                                                 .content(objectMapper.writeValueAsString(USER_LOGIN_DTO)))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(jsonPath("$.accessToken").isNotEmpty())
                    .andExpect(jsonPath("$.expiryAccessToken").isNotEmpty())
                    .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                    .andExpect(jsonPath("$.expiryRefreshToken").isNotEmpty())
                    .andReturn();

            var responseBody = result.getResponse().getContentAsString();
            JwtResponse cred = objectMapper.readValue(responseBody, JwtResponse.class);


            assertNotNull(cred.accessToken(), "Access token should not be null");
            assertNotNull(cred.expiryAccessToken(), "Expiry access token should not be null");
            assertNotNull(cred.refreshToken(), "Refresh token should not be null");
            assertNotNull(cred.expiryRefreshToken(), "Expiry refresh token should not be null");

            return cred;
        }

        @Test
        @Order(3)
        public void api_getUser_isOk() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                                    .get(appComponent.glueEndpoint("/user/" + user.getId()))
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.username").isNotEmpty())
                    .andExpect(jsonPath("$.password").doesNotExist())
                    .andExpect(jsonPath("$.createdDate").doesNotExist())
                    .andExpect(jsonPath("$.lastUpdateDate").doesNotExist())
                    .andReturn();


        }

        @Test
        @Order(4)
        public void api_getMyselfUser_isOk() throws Exception {
            getMyselfUser(credential)
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.username").isNotEmpty())
                    .andExpect(jsonPath("$.password").doesNotExist())
                    .andExpect(jsonPath("$.createdDate").isNotEmpty())
                    .andExpect(jsonPath("$.lastUpdateDate").isNotEmpty())
                    .andReturn();
        }

        private ResultActions getMyselfUser(JwtResponse credential) throws Exception {
            return mockMvc.perform(MockMvcRequestBuilders
                                           .get(appComponent.glueEndpoint("/user"))
                                           .header("Authorization", "Bearer " + credential.accessToken())
                                           .contentType(MediaType.APPLICATION_JSON));
        }

        @Test
        @Order(5)
        public void api_updateAccessToken_isOk() throws Exception {
            updateAccessToken()
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        @Test
        @Order(6)
        public void api_login_beforeChangePassword_isOk() throws Exception {
            credentialSecondLogin = login_isOk();
        }

        @Test
        @Order(7)
        public void api_updateDescriptorAndPassword_isOk() throws Exception {
            var json = """
                    {
                      "description": "description",
                      "password": "new-password"
                    }
                    """;
            var result = mockMvc.perform(MockMvcRequestBuilders
                                                 .put(appComponent.glueEndpoint("/user"))
                                                 .header("Authorization", "Bearer " + credential.accessToken())
                                                 .contentType(MediaType.APPLICATION_JSON)
                                                 .content(json))
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.username").isNotEmpty())
                    .andExpect(jsonPath("$.password").doesNotExist())
                    .andExpect(jsonPath("$.createdDate").isNotEmpty())
                    .andExpect(jsonPath("$.lastUpdateDate").isNotEmpty())
                    .andReturn();

            var responseBody = result.getResponse().getContentAsString();
            var updatedDto = objectMapper.readValue(responseBody, UserDto.class);

            assertEquals("description", updatedDto.getDescription());
            assertNotEquals(user.getLastUpdateDate(), updatedDto.getCreatedDate());
            USER_LOGIN_DTO.setPassword("new-password");
        }

        @Test
        @Order(8)
        public void api_checkNotValidJWT_afterChangePassword_isForbidden() throws Exception {
            getMyselfUser(credentialSecondLogin)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());

            credentialSecondLogin = null;
        }

        private ResultActions updateAccessToken() throws Exception {
            var json = String.format("""
                                             {
                                               "refresh": "%s"
                                             }
                                             """, credential.refreshToken());

            return mockMvc.perform(MockMvcRequestBuilders
                                           .post(appComponent.glueEndpoint("/jwt/refresh"))
                                           .contentType(MediaType.APPLICATION_JSON)
                                           .content(json));
        }

        @Test
        @Order(9)
        public void api_loginWithNewPassword_isOk() throws Exception {
            api_login_isOk();
        }


        @Test
        @Order(10)
        public void api_deleteUser_isOk() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                                    .delete(appComponent.glueEndpoint("/user"))
                                    .header("Authorization", "Bearer " + credential.accessToken())
                                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }

        @Test
        @Order(11)
        public void api_getMyself_afterDeleteAccount_isForbidden() throws Exception {
            getMyselfUser(credential)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        @Order(12)
        public void api_login_afterDeletedAccount_isNotFound() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                                    .post(appComponent.glueEndpoint("/jwt/login"))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(USER_LOGIN_DTO)))
                    .andExpect(MockMvcResultMatchers.status().isNotFound());
        }

        @Test
        @Order(13)
        public void api_restoreUser_isOk() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                                    .patch(appComponent.glueEndpoint("/user/restore"))
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(USER_LOGIN_DTO)))
                    .andExpect(MockMvcResultMatchers.status().isOk());
        }


        // -----------LOGOUT----------------

        @Test
        @Order(14)
        public void api_login_afterRestoreUser_isOk() throws Exception {
            api_login_isOk();
        }

        @Test
        @Order(15)
        public void api_logout_isOk() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders
                                    .post(appComponent.glueEndpoint("/jwt/logout"))
                                    .header("Authorization", "Bearer " + credential.accessToken()))
                    .andExpect(MockMvcResultMatchers.status().isOk());

        }


        @Test
        @Order(16)
        public void api_updateAccessToken_afterLogout_isForbidden() throws Exception {
            updateAccessToken()
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        @Test
        @Order(17)
        public void api_getMyself_afterLogout_isForbidden() throws Exception {
            getMyselfUser(credential)
                    .andExpect(MockMvcResultMatchers.status().isForbidden());
        }

        // -----------LOGOUT----------------


    }


    @Nested
    @DisplayName("Create User - Negative Scenarios")
    class CreateUserBadOperations {

        private ResultActions createUser(String username, String password) throws Exception {
            var dto = new LoginRequest(username, password);
            String str = objectMapper.writeValueAsString(dto);
            return mockMvc.perform(
                    MockMvcRequestBuilders.post(appComponent.glueEndpoint("/user/create"))
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