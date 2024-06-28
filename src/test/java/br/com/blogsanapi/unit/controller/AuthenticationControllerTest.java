package br.com.blogsanapi.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.com.blogsanapi.configs.ControllerUnitTest;
import br.com.blogsanapi.controller.AuthenticationController;
import br.com.blogsanapi.infra.security.TokenService;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.model.user.UserRole;
import br.com.blogsanapi.model.user.auth.AuthenticationDTO;
import br.com.blogsanapi.model.user.auth.RegisterDTO;
import br.com.blogsanapi.repository.UserRepository;
import br.com.blogsanapi.service.UserDetailsServiceImpl;

@ControllerUnitTest
@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<AuthenticationDTO> authenticationDTOJson;
    @Autowired
    private JacksonTester<RegisterDTO> registerDTOJson;

    @MockBean
    private UserRepository repository;
    @MockBean
    private UserDetailsServiceImpl userDetailsServiceImpl;
    @MockBean
    private AuthenticationManager authenticationManager;
    @MockBean
    private TokenService tokenService;

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
    @DisplayName("Unit - Login - Should return status 200 and valid token")
    void loginTest() throws Exception {
        // arrange
        var requestBody = new AuthenticationDTO("userAdminLogin", "userAdminPassword");
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_ADMIN"));
        var authentication = new UsernamePasswordAuthenticationToken(userMock, "userAdminPassword", authorities);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenService.generateToken(any(User.class))).thenReturn("mocked-token");

        // act
        mvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authenticationDTOJson.write(requestBody).getJson())
            )
            // assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("mocked-token"));
    }


    @Test
    @DisplayName("Unit - Register Test - Should return status 200")
    void registerTest01() throws Exception {
        // arrange
        var requestBody = new RegisterDTO("newUser", "test", "Name Test", "email@test.com");
        when(repository.save(any())).thenReturn(userMock);

        // act
        mvc.perform(
            post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerDTOJson.write(requestBody).getJson())
            )
            // assert
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Unit - Admin Register Test 01 - Should return status 200")
    @WithMockUser(roles = "ADMIN")
    void adminRegisterTest01() throws Exception {
        // arrange
        var requestBody = new RegisterDTO("newUser", "test", "Name Test", "email@test.com");
        when(repository.save(any())).thenReturn(userMock);

        // act
        mvc.perform(
            post("/auth/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerDTOJson.write(requestBody).getJson())
            )
            // assert
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Unit - Admin Register Test 02 - Should return status 403")
    @WithMockUser(roles = "CLIENT")
    void adminRegisterTest02() throws Exception {
        // arrange
        var requestBody = new RegisterDTO("newUser", "test", "Name Test", "email@test.com");
        when(repository.save(any())).thenReturn(userMock);

        // act
        mvc.perform(
            post("/auth/admin/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerDTOJson.write(requestBody).getJson())
            )
            // assert
            .andExpect(status().isForbidden());
    }
}