package br.com.blogsanapi.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.com.blogsanapi.configs.UnitTest;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.model.user.UserRole;
import br.com.blogsanapi.model.user.auth.AuthenticationDTO;
import br.com.blogsanapi.model.user.auth.LoginResponseDTO;
import br.com.blogsanapi.model.user.auth.RegisterDTO;
import br.com.blogsanapi.repository.UserRepository;
import br.com.blogsanapi.service.UserDetailsServiceImpl;
import br.com.blogsanapi.utils.TokenUtils;

@UnitTest
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<AuthenticationDTO> authenticationDTOJson;
    @Autowired
    private JacksonTester<RegisterDTO> registerDTOJson;
    @Autowired
    private JacksonTester<LoginResponseDTO> loginResponseDTOJson;

    @MockBean
    private UserRepository repository;
    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;

    // Mocked User
    private final User userMock = User.builder()
            .id(1L)
            .login("userAdminLogin")
            .password(new BCryptPasswordEncoder().encode("userAdminPassword"))
            .role(UserRole.ADMIN)
            .name("Name Default")
            .email("default@email.com")
            .build();

    @Test
    @DisplayName("Login Test - Should return valid token")
    void loginTest() throws Exception {
        // arrange
        var requestBody = new AuthenticationDTO("userAdminLogin", "userAdminPassword");
        when(userDetailsServiceImpl.loadUserByUsername(any())).thenReturn(userMock);

        // act
        var result = mvc.perform(
                post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(authenticationDTOJson.write(requestBody).getJson()))
                .andReturn().getResponse();
        var responseBody = loginResponseDTOJson.parseObject(result.getContentAsString());

        // assert
        Assertions.assertEquals(HttpStatus.OK.value(), result.getStatus(), "The status should be 200 OK");
        Assertions.assertNotNull(responseBody.token(), "The token should not be null");
        Assertions.assertFalse(responseBody.token().isBlank(), "The token should not be blank");
        Assertions.assertTrue(TokenUtils.isValidTokenFormat(responseBody.token()), "The token format should be valid");
    }

    @Test
    @DisplayName("Register Test - Should return OK status")
    void registerTest() throws Exception {
        // arrange
        var requestBody = new RegisterDTO("newUser", "test", "Name Test", "email@test.com");
        when(repository.save(any())).thenReturn(userMock);

        // act
        var result = mvc.perform(
                post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerDTOJson.write(requestBody).getJson()))
                .andReturn().getResponse();

        // assert
        Assertions.assertEquals(HttpStatus.OK.value(), result.getStatus(), "The status should be 200 OK");
    }

    @Test
    @DisplayName("Admin Register Test 01 - Should return OK status")
    @WithMockUser(roles = "ADMIN")
    void adminRegisterTest01() throws Exception {
        // arrange
        var requestBody = new RegisterDTO("newUser", "test", "Name Test", "email@test.com");
        when(repository.save(any())).thenReturn(userMock);

        // act
        var result = mvc.perform(
                post("/auth/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerDTOJson.write(requestBody).getJson()))
                .andReturn().getResponse();

        // assert
        Assertions.assertEquals(HttpStatus.OK.value(), result.getStatus(), "The status should be 200 OK");
    }

    @Test
    @DisplayName("Admin Register Test 02 - Should return FORBIDDEN status")
    @WithMockUser(roles = "CLIENT")
    void adminRegisterTest02() throws Exception {
        // arrange
        var requestBody = new RegisterDTO("newUser", "test", "Name Test", "email@test.com");
        when(repository.save(any())).thenReturn(userMock);

        // act
        var result = mvc.perform(
                post("/auth/admin/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerDTOJson.write(requestBody).getJson()))
                .andReturn().getResponse();

        // assert
        Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), result.getStatus(), "The status should be 403 FORBIDDEN");
    }
}