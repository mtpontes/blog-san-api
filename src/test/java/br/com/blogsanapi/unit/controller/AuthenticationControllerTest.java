package br.com.blogsanapi.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import br.com.blogsanapi.utils.ControllerTestUtils;

@ControllerUnitTest
@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {

    private final String BASE_URL = "/auth";

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
        var makeLogin = new AuthenticationDTO("userAdminLogin", "userAdminPassword");
        String requestBody = authenticationDTOJson.write(makeLogin).getJson();

        var authorities = List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_ADMIN"));
        var authentication = new UsernamePasswordAuthenticationToken(userMock, "userAdminPassword", authorities);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenService.generateToken(any(User.class))).thenReturn("mocked-token");

        // act
        ControllerTestUtils.postRequest(mvc, (this.BASE_URL + "/login"), requestBody)
            // assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("mocked-token"));
    }


    @Test
    @DisplayName("Unit - Register Test - Should return status 200")
    void registerTest01() throws Exception {
        // arrange
        var createUser = new RegisterDTO("newUser", "test", "Name Test", "email@test.com");
        String requestBody = registerDTOJson.write(createUser).getJson();

        when(repository.save(any())).thenReturn(userMock);

        // act
        ControllerTestUtils.postRequest(mvc, (this.BASE_URL + "/register"), requestBody)
            // assert
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Unit - Admin Register Test 01 - Should return status 200")
    @WithMockUser(roles = "ADMIN")
    void adminRegisterTest01() throws Exception {
        // arrange
        var registerAdmin = new RegisterDTO("newUser", "test", "Name Test", "email@test.com");
        String requestBody = registerDTOJson.write(registerAdmin).getJson();

        when(repository.save(any())).thenReturn(userMock);

        // act
        ControllerTestUtils.postRequest(mvc, (this.BASE_URL + "/admin/register"), requestBody)
            // assert
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Unit - Admin Register Test 02 - Should return status 403")
    @WithMockUser(roles = "CLIENT")
    void adminRegisterTest02() throws Exception {
        // arrange
        var registerAdmin = new RegisterDTO("newUser", "test", "Name Test", "email@test.com");
        String requestBody = registerDTOJson.write(registerAdmin).getJson();

        when(repository.save(any())).thenReturn(userMock);

        // act
        ControllerTestUtils.postRequest(mvc, (this.BASE_URL + "/admin/register"), requestBody)
            // assert
            .andExpect(status().isForbidden());
    }
}