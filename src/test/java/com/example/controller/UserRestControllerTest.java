package com.example.controller;

import com.example.dto.LoginRequest;
import com.example.util.ApplicationDataComponent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserRestControllerTest {


    private final MockMvc mockMvc;
    private final ObjectMapper mapper;
    private final ApplicationDataComponent applicationDataComponent;

    @Value("${app.version}")
    private String path;


    @Autowired
    public UserRestControllerTest(MockMvc mockMvc, ObjectMapper mapper,
                                  ApplicationDataComponent applicationDataComponent) {
        this.mockMvc = mockMvc;
        this.mapper = mapper;
        this.applicationDataComponent = applicationDataComponent;

    }

    @Test
    public void testCreateUser() throws Exception {
        var userDto = new LoginRequest("alexandr", "password");
        String str = mapper.writeValueAsString(userDto);
        System.out.println(str);
        mockMvc.perform(MockMvcRequestBuilders.post(
                                "/api/v" + path + "/user/create") // todo отличия в app.properties и штуке что я использую
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(str))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("alexandr"));
        //                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }

    //    @Test
    //    public void testGetUser() throws Exception {
    //        // Сначала создаем пользователя
    //        UserDto userDto = new UserDto();
    //        userDto.setName("John");
    //        userDto.setEmail("john@example.com");
    //
    //        MvcResult result = mockMvc.perform(post("/api/v1/user/create")
    //                                                   .contentType(MediaType.APPLICATION_JSON)
    //                                                   .content(mapper.writeValueAsString(userDto)))
    //                .andReturn();
    //
    //        String response = result.getResponse().getContentAsString();
    //        UserDto createdUser = mapper.readValue(response, UserDto.class);
    //
    //        // Получаем пользователя по id
    //        mockMvc.perform(get("/api/v1/user/" + createdUser.getId()))
    //                .andExpect(status().isOk())
    //                .andExpect(jsonPath("$.name").value("John"))
    //                .andExpect(jsonPath("$.email").value("john@example.com"));
    //    }

}