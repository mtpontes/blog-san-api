package br.com.blogsanapi.integration.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import br.com.blogsanapi.configs.E2ETest;
import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.comment.request.CommentRequestDTO;
import br.com.blogsanapi.model.publication.Publication;
import br.com.blogsanapi.model.publication.request.PublicationRequestDTO;
import br.com.blogsanapi.model.publication.request.PublicationUpdateDTO;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.model.user.UserRole;
import br.com.blogsanapi.model.user.auth.AuthenticationDTO;
import br.com.blogsanapi.repository.CommentRepository;
import br.com.blogsanapi.repository.PublicationRepository;
import br.com.blogsanapi.repository.UserRepository;

@E2ETest
public class PublicationControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<PublicationRequestDTO> publicationRequestDTOJson;
    @Autowired
    private JacksonTester<PublicationUpdateDTO> publicationUpdateDTOJson;

    @Autowired
    private JacksonTester<CommentRequestDTO> commentRequestDTOJson;

    @Autowired
    private JacksonTester<AuthenticationDTO> authenticationDTOJson;

    @BeforeAll
    static void setup(
        @Autowired UserRepository userRepository,
        @Autowired PublicationRepository publicationRepository,
        @Autowired CommentRepository commentRepository,
        @Autowired PasswordEncoder encoder,
        @Autowired MockMvc mvc) throws Exception {
            
        List<User> users = List.of(
            User.builder().name("tester").login("test").password(encoder.encode("test")).role(UserRole.ADMIN).build()
            );
        userRepository.saveAll(users);

        List<Publication> publications = List.of(
            Publication.builder().description("description-1").imageLink("link-1").user(users.get(0)).build(),
            Publication.builder().description("description-2").imageLink("link-2").user(users.get(0)).build(),
            Publication.builder().description("description-3").imageLink("link-3").user(users.get(0)).build()
            );
        publicationRepository.saveAll(publications);

        List<Comment> comments = List.of(
            Comment.builder().text("c1").user(users.get(0)).publication(publications.get(0)).build(),
            Comment.builder().text("c2").user(users.get(0)).publication(publications.get(0)).build(),
            Comment.builder().text("c3").user(users.get(0)).publication(publications.get(0)).build(),
            Comment.builder().text("c4").user(users.get(0)).publication(publications.get(0)).build(),
            Comment.builder().text("c5").user(users.get(0)).publication(publications.get(0)).build()
            );
        commentRepository.saveAll(comments);
    }

    private String getTokenValue(String token) {
        return token
            .split(":")[1]
            .replace("\"", "")
            .replace("}", "");
    }

    private String makeLoginAndGetToken() throws Exception {
        var response = mvc.perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(authenticationDTOJson.write(new AuthenticationDTO("test", "test")).getJson())
            )
            // assert
            .andReturn().getResponse();

        return getTokenValue(response.getContentAsString());
    }

    @Test
    @DisplayName("Integration - Create Publication Test 01 - Should return new publication details")
    void createPublicationTest01() throws Exception {
        // arrange
        var requestBody = new PublicationRequestDTO("Testing", "imageLink");

        // act
        mvc.perform(
            post("/publications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(publicationRequestDTOJson.write(requestBody).getJson())
                .header("Authorization", "Bearer " + this.makeLoginAndGetToken())
            )
            // assert
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.publicationId").isNotEmpty())
            .andExpect(jsonPath("$.userId").isNotEmpty())
            .andExpect(jsonPath("$.nameUser").isNotEmpty())
            .andExpect(jsonPath("$.description").isNotEmpty())
            .andExpect(jsonPath("$.imageLink").isNotEmpty())
            .andExpect(jsonPath("$.date").isNotEmpty())
            .andExpect( result -> this.validateRedirectUrl(result));
    }
    @Test
    @DisplayName("Integration - Create Publication Test 02 - Should return status 201")
    void createPublicationTest02() throws Exception {
        // arrange
        var requestBodyWithDescriptionEmpty = new PublicationRequestDTO("", "imageLink");

        // act
        mvc.perform(
            post("/publications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(publicationRequestDTOJson.write(requestBodyWithDescriptionEmpty).getJson())
                .header("Authorization", "Bearer " + this.makeLoginAndGetToken())
            )
            // assert
            .andExpect(status().isCreated());

        var requestBodyWithImageLinkEmpty = new PublicationRequestDTO("description", "");

        // act
        mvc.perform(
            post("/publications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(publicationRequestDTOJson.write(requestBodyWithImageLinkEmpty).getJson())
                .header("Authorization", "Bearer " + this.makeLoginAndGetToken())
            )
            // assert
            .andExpect(status().isCreated());
    }
    @Test
    @DisplayName("Integration - Create Publication Test 03 - Should return status 400 when description and imageLink is simultaneously blank")
    void createPublicationTest03() throws Exception {
        // arrange
        var emptyRequestBody = new PublicationRequestDTO("", "");

        // act
        mvc.perform(
            post("/publications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(publicationRequestDTOJson.write(emptyRequestBody).getJson())
                .header("Authorization", "Bearer " + this.makeLoginAndGetToken())
            )
            // assert
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fields.imageLink").exists())
            .andExpect(jsonPath("$.fields.description").exists());
    }

    @Test
    @DisplayName("Integration - Update Publication Test 01 - Should return status 200 and updated publication details")
    void updatePublicationTest01() throws Exception {
        // arrange
        var requestBody = new PublicationUpdateDTO("description updated");

        // act
        mvc.perform(
            patch("/publications/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(publicationUpdateDTOJson.write(requestBody).getJson())
                .header("Authorization", "Bearer " + this.makeLoginAndGetToken())
            )
            // assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.publicationId").exists())
            .andExpect(jsonPath("$.userId").exists())
            .andExpect(jsonPath("$.nameUser").exists())
            .andExpect(jsonPath("$.description").exists())
            .andExpect(jsonPath("$.imageLink").exists())
            .andExpect(jsonPath("$.date").exists());
    }
    @Test
    @DisplayName("Integration - Update Publication Test 02 - Should throw exception when not finding Publication")
    void updatePublicationTest02() throws Exception {
        // arrange
        var requestBody = new PublicationUpdateDTO("description updated");

        // act
        mvc.perform(
            patch("/publications/459283492384") // invalid ID
                .contentType(MediaType.APPLICATION_JSON)
                .content(publicationUpdateDTOJson.write(requestBody).getJson())
                .header("Authorization", "Bearer " + this.makeLoginAndGetToken())
            )
            // assert
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Integration - Delete Publication Test 01 - Should return status 204")
    void deletePublicationTest01() throws Exception {
        // act
        mvc.perform(
            delete("/publications/3")
                .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer " + this.makeLoginAndGetToken())
            )
            // assert
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Integration - Create Comment Test 01 - Should return status 201 and comment data")
    void createCommentTest01() throws Exception {
        // arrange
        var requestBody = new CommentRequestDTO("Testing");

        // act
        mvc.perform(
            post("/publications/1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentRequestDTOJson.write(requestBody).getJson())
                .header("Authorization", "Bearer " + this.makeLoginAndGetToken())
            )
            // assert
            .andExpect(status().isCreated())
            .andExpect(result -> this.validateRedirectUrl(result))
            .andExpect(jsonPath("$.commentId").isNotEmpty())
            .andExpect(jsonPath("$.userId").isNotEmpty())
            .andExpect(jsonPath("$.nameUser").isNotEmpty())
            .andExpect(jsonPath("$.text").isNotEmpty())
            .andExpect(jsonPath("$.date").isNotEmpty())
            .andExpect(jsonPath("$.edited").isBoolean())
            .andExpect(jsonPath("$.parentCommentId").isEmpty());
    }
    @Test
    @DisplayName("Integration - Create Comment Test 02 - Should return status 400 and field errors")
    void createCommentTest02() throws Exception {
        // arrange
        var requestBody = new CommentRequestDTO("");

        // act
        mvc.perform(
            post("/publications/1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentRequestDTOJson.write(requestBody).getJson())
                .header("Authorization", "Bearer " + this.makeLoginAndGetToken())
            )
            // assert
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fields.text").exists());
    }
    @Test
    @DisplayName("Integration - Create Comment Test 03 - Should return status 400 when comment is not found")
    void createCommentTest03() throws Exception {
        // arrange
        var requestBody = new CommentRequestDTO("");

        // act
        mvc.perform(
            post("/publications/104593409593405/comments") // invalid ID
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentRequestDTOJson.write(requestBody).getJson())
                .header("Authorization", "Bearer " + this.makeLoginAndGetToken())
            )
            // assert
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integration - Create Reply Test 01 - Should return status 201 and new reply details")
    void createReplyTest01() throws Exception {
        // arrange
        var requestBody = new CommentRequestDTO("Testing");

        // act
        mvc.perform(
            post("/publications/comments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentRequestDTOJson.write(requestBody).getJson())
                .header("Authorization", "Bearer " + this.makeLoginAndGetToken())
            )
            // assert
            .andExpect( result -> this.validateRedirectUrl(result))
            .andExpect(status().isCreated());
    }
    @Test
    @DisplayName("Integration - Create Reply Test 02 - Should return status 400 when 'text' is empty")
    void createReplyTest02() throws Exception {
        // arrange
        var requestBody = new CommentRequestDTO("");

        // act
        mvc.perform(
            post("/publications/comments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentRequestDTOJson.write(requestBody).getJson())
                .header("Authorization", "Bearer " + this.makeLoginAndGetToken())
            )
            // assert
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integration - Update Comment Test 01 - Should return status 200 and updated comment details")
    void updateCommentTest01() throws Exception {
        // arrange
        var requestBody = new CommentRequestDTO("description updated");

        // act
        mvc.perform(
            patch("/publications/comments/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentRequestDTOJson.write(requestBody).getJson())
                .header("Authorization", "Bearer " + this.makeLoginAndGetToken())
            )
            // assert
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.text").isNotEmpty());
    }
    @Test
    @DisplayName("Integration - Update Comment Test 02 - Should return status 400 when 'text' is empty")
    void updateCommentTest02() throws Exception {
        // arrange
        var requestBody = new CommentRequestDTO("");

        // act
        mvc.perform(
            patch("/publications/comments/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(commentRequestDTOJson.write(requestBody).getJson())
                .header("Authorization", "Bearer " + this.makeLoginAndGetToken())
            )
            // assert
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.fields.text").isNotEmpty());
    }

    @Test
    @DisplayName("Integration - Delete Comment Test 01 - Should return status 204")
    void deleteCommentTest01() throws Exception {
        // act
        mvc.perform(
            delete("/publications/comments/3")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + this.makeLoginAndGetToken())
            )
            // assert
            .andExpect(status().isNoContent());
    }

    void validateRedirectUrl(MvcResult result) {        
        String redirect = result.getResponse().getRedirectedUrl();
        var nullableRedicrect = Optional.ofNullable(redirect);

        redirect = nullableRedicrect
            .orElseThrow(() -> new AssertionError("Redirect URL is null"));
        if (redirect.isBlank())
            throw new AssertionError("Redirect URL is blank");
    }
}