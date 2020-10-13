package com.jpandres.docuuserservice.controller;

import com.jpandres.docuuserservice.data.UserVO;
import com.jpandres.docuuserservice.exception.DuplicatedUserException;
import com.jpandres.docuuserservice.model.User;
import com.jpandres.docuuserservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.LOCATION;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = {UserController.class})
@ExtendWith(value = {RestDocumentationExtension.class})
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(documentationConfiguration(restDocumentation).operationPreprocessors()
                               .withResponseDefaults(prettyPrint()))
                .build();
    }

    @Test
    @DisplayName("Given a valid User " +
                         "When posting to /users" +
                         " Then is should create it and return the URL of the new resource")
    void shouldCreatUser() throws Exception {
        var userId = UUID.randomUUID();
        when(userService.createUser(any())).thenReturn(User.builder().id(userId).build());

        var request = RestDocumentationRequestBuilders.post("/api/v1/users")
                .content("""
                                 {
                                 "username" : "john.doe@gmail.com",
                                 "firstname": "John",
                                 "lastname": "Doe",
                                 "age" : 33
                                 } 
                                 """)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string(LOCATION, "/api/v1/users/" + userId))
                .andDo(document("users-post",
                                requestFields(
                                        fieldWithPath("username").type(JsonFieldType.STRING)
                                                .description(
                                                        "Username (email format preferred)"),
                                        fieldWithPath("firstname").type(JsonFieldType.STRING)
                                                .description("User firstname"),
                                        fieldWithPath("lastname")
                                                .type(JsonFieldType.STRING)
                                                .description(
                                                        "User lastname"),
                                        fieldWithPath("age").type(JsonFieldType.NUMBER).optional().description(
                                                "User Age")
                                ),
                                responseHeaders(headerWithName(LOCATION).description(
                                        "URI of the new created resource."))));
    }

    @Test
    @DisplayName("Given a valid User id" +
                         "When getting a user" +
                         " Then is should return the user")
    void shouldGetUser() throws Exception {
        var userId = UUID.randomUUID();
        when(userService.getUser(userId.toString())).thenReturn(UserVO.builder()
                                                                        .username("john.doe@gmail.com")
                                                                        .firstname("John")
                                                                        .lastname("Doe")
                                                                        .age(34)
                                                                        .build());

        var request = RestDocumentationRequestBuilders.get("/api/v1/users/{userid}", userId)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-get",
                                pathParameters(
                                        parameterWithName("userid").description(
                                                "The User Id (UUID)")
                                ),
                                responseFields(
                                        fieldWithPath("username").type(JsonFieldType.STRING)
                                                .description(
                                                        "Username (email format preferred)"),
                                        fieldWithPath("firstname").type(JsonFieldType.STRING)
                                                .description("User firstname"),
                                        fieldWithPath("lastname")
                                                .type(JsonFieldType.STRING)
                                                .description(
                                                        "User lastname"),
                                        fieldWithPath("age").type(JsonFieldType.NUMBER).optional().description(
                                                "User Age")
                                ),
                                responseHeaders(headerWithName(CONTENT_TYPE)
                                                        .description("The Content-Type of the payload"))));

    }

    @Test
    @DisplayName("Given a valid request" +
                         "When getting all users" +
                         " Then is should return a list of  users")
    void shouldGetUsers() throws Exception {
        when(userService.getUsers()).thenReturn(List.of(
                UserVO.builder()
                        .username("john.doe@gmail.com")
                        .firstname("John")
                        .lastname("Doe")
                        .age(34)
                        .build(),
                UserVO.builder()
                        .username("pepe@gmail.com")
                        .firstname("Pepe")
                        .lastname("Doe")
                        .age(32)
                        .build()
        ));

        var request = RestDocumentationRequestBuilders.get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(document("users-get",
                                responseFields(
                                        fieldWithPath("[].username").type(JsonFieldType.STRING)
                                                .description(
                                                        "Username (email format preferred)"),
                                        fieldWithPath("[].firstname").type(JsonFieldType.STRING)
                                                .description("User firstname"),
                                        fieldWithPath("[].lastname")
                                                .type(JsonFieldType.STRING)
                                                .description(
                                                        "User lastname"),
                                        fieldWithPath("[].age").type(JsonFieldType.NUMBER).optional().description(
                                                "User Age")
                                ),
                                responseHeaders(headerWithName(CONTENT_TYPE)
                                                        .description("The Content-Type of the payload"))));

    }

    @Test
    @DisplayName("Given an invalid request" +
                         "When creating a user" +
                         " Then is should return a 400 with specific message")
    void shouldReturnBadRequest() throws Exception {
        var userId = UUID.randomUUID();

        var request = RestDocumentationRequestBuilders.post("/api/v1/users")
                .content("""
                                 {
                                 "username" : "",
                                 "firstname": "John",
                                 "lastname": "Doe",
                                 "age" : -2
                                 } 
                                 """)
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andDo(document("user-error",
                                requestFields(
                                        fieldWithPath("username").type(JsonFieldType.STRING)
                                                .description(
                                                        "Username (email format preferred)"),
                                        fieldWithPath("firstname").type(JsonFieldType.STRING)
                                                .description("User firstname"),
                                        fieldWithPath("lastname")
                                                .type(JsonFieldType.STRING)
                                                .description(
                                                        "User lastname"),
                                        fieldWithPath("age").type(JsonFieldType.NUMBER).optional().description(
                                                "User Age")
                                ),
                                responseFields(
                                        fieldWithPath("message").type(JsonFieldType.STRING)
                                                .description(
                                                        "HTTP Status Error message"),
                                        fieldWithPath("details").type(JsonFieldType.ARRAY)
                                                .description("List of errors"),
                                        fieldWithPath("details[].code")
                                                .type(JsonFieldType.STRING)
                                                .description(
                                                        "Error Code: <Error type>.<field name>"),
                                        fieldWithPath("details[].description")
                                                .type(JsonFieldType.STRING)
                                                .description(
                                                        "Error Detailed Description")
                                )));
    }

    @Test
    void shouldReturn500() throws Exception {
        when(userService.getUsers()).thenThrow(new RuntimeException("some errro"));

        var request = get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }

    @Test
    void shouldReturn409() throws Exception {
        when(userService.getUsers()).thenThrow(new DuplicatedUserException("some errro"));

        var request = get("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON);

        mockMvc.perform(request)
                .andDo(print())
                .andExpect(status().isConflict());
    }
}