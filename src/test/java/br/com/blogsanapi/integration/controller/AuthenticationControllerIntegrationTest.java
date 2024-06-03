package br.com.blogsanapi.integration.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import br.com.blogsanapi.configs.IntegrationTest;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.model.user.UserRole;
import br.com.blogsanapi.model.user.auth.AuthenticationDTO;
import br.com.blogsanapi.model.user.auth.LoginResponseDTO;
import br.com.blogsanapi.model.user.auth.RegisterDTO;
import br.com.blogsanapi.repository.UserRepository;
import br.com.blogsanapi.utils.TokenUtils;

@IntegrationTest
public class AuthenticationControllerIntegrationTest {

        @Autowired
        private MockMvc mvc;

        @Autowired
        private JacksonTester<AuthenticationDTO> authenticationDTOJson;
        @Autowired
        private JacksonTester<RegisterDTO> registerDTOJson;
        @Autowired
        private JacksonTester<LoginResponseDTO> loginResponseDTOJson;

        @BeforeAll
        static void setup(
                @Autowired UserRepository repository, 
                @Autowired BCryptPasswordEncoder encoder) throws Exception {

                List<User> users = List.of(
                                User.builder()
                                        .name("Admin-san")
                                        .login("adminLogin")
                                        .password(encoder.encode("adminPassword"))
                                        .role(UserRole.ADMIN)
                                        .build(),

                                User.builder()
                                        .name("Client-san")
                                        .login("clientLogin")
                                        .password(encoder.encode("clientPassword"))
                                        .role(UserRole.CLIENT)
                                        .build());
                repository.saveAll(users);
        }

        @Test
        @DisplayName("Login Test - Should return valid token")
        void loginTest() throws Exception {
                // arrange
                var requestBody = new AuthenticationDTO("adminLogin", "adminPassword");

                // act
                var result = mvc.perform(
                                post("/auth/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(authenticationDTOJson.write(requestBody).getJson()))
                                .andReturn().getResponse();

                var responseBody = loginResponseDTOJson.parseObject(result.getContentAsString());

                // assert
                Assertions.assertNotNull(responseBody.token(), "The token should not be null");
                Assertions.assertFalse(responseBody.token().isBlank(), "The token should not be blank");
                Assertions.assertTrue(TokenUtils.isValidTokenFormat(responseBody.token()),
                                "The token format should be valid");
        }

        @Test
        @DisplayName("Register Test - Should return OK status")
        void registerTest() throws Exception {
                // arrange
                var requestBody = new RegisterDTO("registerTest", "registerTest", "Register Test-san", "email@registertest.com");

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
        void adminRegisterTest01() throws Exception {
                // arrange
                var requestBodyToLogin = new AuthenticationDTO("adminLogin", "adminPassword");
                var tokenJson = mvc.perform(
                                post("/auth/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(authenticationDTOJson.write(requestBodyToLogin).getJson()))
                                .andReturn().getResponse();
                var token = loginResponseDTOJson.parseObject(tokenJson.getContentAsString()).token();

                var requestBodyToCreateAdmin = new RegisterDTO("newUser", "test", "Name Test", "email@test.com");

                // act
                var result = mvc.perform(
                                post("/auth/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(registerDTOJson.write(requestBodyToCreateAdmin).getJson())
                                                .header("Authorization", "Bearer " + token))
                                .andReturn().getResponse();

                // assert
                Assertions.assertEquals(HttpStatus.OK.value(), result.getStatus(), "The status should be 200 OK");
        }

        @Test
        @DisplayName("Admin Register Test 02 - Should return FORBIDDEN status")
        void adminRegisterTest02() throws Exception {
                // arrange
                var requestBodyToLogin = new AuthenticationDTO("clientLogin", "clientPassword");
                var tokenJson = mvc.perform(
                                post("/auth/login")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(authenticationDTOJson.write(requestBodyToLogin).getJson()))
                                .andReturn().getResponse();
                var token = loginResponseDTOJson.parseObject(tokenJson.getContentAsString()).token();

                var requestBodyToCreateAdmin = new RegisterDTO("newUser", "test", "Name Test", "email@test.com");

                // act
                var result = mvc.perform(
                                post("/auth/admin/register")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content(registerDTOJson.write(requestBodyToCreateAdmin).getJson())
                                                .header("Authorization", "Bearer " + token))
                                .andReturn().getResponse();

                // assert
                Assertions.assertEquals(HttpStatus.FORBIDDEN.value(), result.getStatus(),
                                "The status should be 403 FORBIDDEN");
        }
}