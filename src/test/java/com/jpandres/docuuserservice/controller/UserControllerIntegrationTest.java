package com.jpandres.docuuserservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    protected WebApplicationContext context;
    @Autowired
    protected MockMvc mockMvc;

    @Test
    @DisplayName("Given a valid user " +
                         "When creating it" +
                         "Then it should return the Location URL")
    void shouldCreateUserAndGetIt() throws Exception {

        var request = post("/api/v1/users")
                .content("""
                                 {
                                 "username" : "john.doe@gmail.com",
                                 "firstname": "John",
                                 "lastname": "Doe",
                                 "age" : 33
                                 } 
                                 """)
                .contentType(MediaType.APPLICATION_JSON);

        String location = mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().exists(LOCATION))
                .andReturn().getResponse().getHeader(LOCATION);

        mockMvc.perform(get(location))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("username").value("john.doe@gmail.com"));
    }

    @Test
    @DisplayName("Given a valid user " +
                         "When creating it" +
                         "Then it should return the Location URL")
    void shouldReturnError() throws Exception {

        var request = post("/api/v1/users")
                .content("""
                                 {
                                 "username" : "",
                                 "firstname": "",
                                 "lastname": "",
                                 "age" : -2
                                 } 
                                 """)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Bad Request"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details", hasSize(4)))
                .andExpect(jsonPath("$.details[0].description").value("age must be greater than 0"))
                .andExpect(jsonPath("$.details[1].description").value(("firstname must not be empty")))
                .andExpect(jsonPath("$.details[2].description").value(("lastname must not be empty")))
                .andExpect(jsonPath("$.details[3].description").value(("username must not be empty")));
    }
}