package br.com.blogsanapi.integration.controller;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import br.com.blogsanapi.configs.E2ETest;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.model.user.UserRole;
import br.com.blogsanapi.model.user.auth.AuthenticationDTO;
import br.com.blogsanapi.model.user.auth.LoginResponseDTO;
import br.com.blogsanapi.model.user.auth.RegisterDTO;
import br.com.blogsanapi.repository.UserRepository;
import br.com.blogsanapi.utils.TokenUtils;

@E2ETest
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
				.login("adminLogin")
				.password(encoder.encode("adminPassword"))
				.name("Admin-san")
				.role(UserRole.ADMIN)
				.build(),

			User.builder()
				.login("clientLogin")
				.password(encoder.encode("clientPassword"))
				.name("Client-san")
				.role(UserRole.CLIENT)
				.build());
		repository.saveAll(users);
	}

	@Test
	@DisplayName("Integration - login test 01 - Should return status 200 and valid token")
	void loginTest() throws Exception {
		// arrange
		var requestBody = new AuthenticationDTO("adminLogin", "adminPassword");

		// act
		var result = mvc.perform(
			post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(authenticationDTOJson.write(requestBody).getJson())
			)
			// assert
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").isNotEmpty())
			.andReturn().getResponse();

        String responseBody = result.getContentAsString().replaceAll(".*\"token\":\\s*\"(.*?)\".*", "$1");

        // assert
        assertTrue(TokenUtils.isValidTokenFormat(responseBody));
	}
	@Test
	@DisplayName("Integration - login test 02 - Should return status 400 when username and password do not meet the expected format")
	void loginTest02() throws IOException, Exception {
		// arrange
		AuthenticationDTO requestBody = new AuthenticationDTO("", "");

		// act
		mvc.perform(
			post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(authenticationDTOJson.write(requestBody).getJson())
			)
			// assert
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.token").doesNotExist())
			.andExpect(jsonPath("$.fields.login").exists())
			.andExpect(jsonPath("$.fields.password").exists());
	}
	@Test
    @DisplayName("Integration - login test 03 - Should return status 401 when user is not found")
	void loginTest03() throws IOException, Exception {
		// arrange
		AuthenticationDTO requestBody = new AuthenticationDTO("non-existent", "adminPassword");

		// act
		mvc.perform(
			post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(authenticationDTOJson.write(requestBody).getJson())
			)
			// assert
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.token").doesNotExist());
	}
	@Test
    @DisplayName("Integration - login test 04 - Should return 401 status when password does not match")
    void loginTest04() throws IOException, Exception {
        // arrange
		AuthenticationDTO requestBody = new AuthenticationDTO("adminLogin", "non-existent");

        // act
        mvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authenticationDTOJson.write(requestBody).getJson())
        )
        // assert
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.token").doesNotExist());
    }

	@Test
	@DisplayName("Integration - register test 01 - Should return status 200")
	void registerTest01() throws Exception {
		// arrange
		var requestBody = new RegisterDTO("registerTest", "registerTest", "Register Test-san", "email@registertest.com");

		// act
		mvc.perform(
			post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(registerDTOJson.write(requestBody).getJson())
			)
			// assert
			.andExpect(status().isOk())
			.andReturn().getResponse();
	}
	@Test
	@DisplayName("Integration - register test 02 - Should return status 400 and field errors")
	void registerTest02() throws Exception {
		// arrange
		var requestBody = new RegisterDTO("", "", "", "emailregistertestcom");

		// act
		mvc.perform(
			post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(registerDTOJson.write(requestBody).getJson())
			)
			// assert
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.fields.login").exists())
			.andExpect(jsonPath("$.fields.password").exists())
			.andExpect(jsonPath("$.fields.name").exists())
			.andExpect(jsonPath("$.fields.email").exists());
	}

	@Test
	@DisplayName("Integration - Admin Register Test 01 - Should return status 200")
	void adminRegisterTest01() throws Exception {
		// arrange
		var requestBodyToLogin = new AuthenticationDTO("adminLogin", "adminPassword");
		var tokenJsonResponse = mvc.perform(
			post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(authenticationDTOJson.write(requestBodyToLogin).getJson())
			)
			.andReturn().getResponse();
		
		var token = loginResponseDTOJson.parseObject(tokenJsonResponse.getContentAsString()).token();

		var requestBodyToCreateAdmin = new RegisterDTO("newUser", "test", "Name Test", "email@test.com");

		// act
		mvc.perform(
			post("/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(registerDTOJson.write(requestBodyToCreateAdmin).getJson())
				.header("Authorization", "Bearer " + token)
			)
			.andExpect(status().isOk());
	}
	@Test
	@DisplayName("Integration - Admin Register Test 02 - Should return FORBIDDEN status when a client tries to register an admin")
	void adminRegisterTest02() throws Exception {
		// arrange
		var requestBodyToLogin = new AuthenticationDTO("clientLogin", "clientPassword");
		var tokenJson = mvc.perform(
			post("/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content(authenticationDTOJson.write(requestBodyToLogin).getJson())
			)
			.andReturn().getResponse();
		var token = loginResponseDTOJson.parseObject(tokenJson.getContentAsString()).token();

		var requestBodyToCreateAdmin = new RegisterDTO("newUser", "test", "Name Test", "email@test.com");

		// act
		mvc.perform(
			post("/auth/admin/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(registerDTOJson.write(requestBodyToCreateAdmin).getJson())
				.header("Authorization", "Bearer " + token)
			)
			.andExpect(status().isForbidden());
	}
	@Test
	@DisplayName("Integration - Admin Register Test 03 - Should return FORBIDDEN when missing authorization header")
	void adminRegisterTest03() throws Exception {
		// arrange
		var requestBodyToCreateAdmin = new RegisterDTO("newUser", "test", "Name Test", "email@test.com");

		// act
		mvc.perform(
			post("/auth/admin/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(registerDTOJson.write(requestBodyToCreateAdmin).getJson())
				// .header("Authorization", "")
			)
			.andExpect(status().isForbidden());
	}
}