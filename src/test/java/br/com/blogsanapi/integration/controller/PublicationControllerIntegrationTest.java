package br.com.blogsanapi.integration.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import br.com.blogsanapi.configs.IntegrationTest;
import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.comment.request.CommentRequestDTO;
import br.com.blogsanapi.model.comment.response.CommentResponseDTO;
import br.com.blogsanapi.model.publication.Publication;
import br.com.blogsanapi.model.publication.request.PublicationRequestDTO;
import br.com.blogsanapi.model.publication.request.PublicationUpdateDTO;
import br.com.blogsanapi.model.publication.response.PublicationResponseDTO;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.model.user.UserRole;
import br.com.blogsanapi.model.user.auth.AuthenticationDTO;
import br.com.blogsanapi.repository.CommentRepository;
import br.com.blogsanapi.repository.PublicationRepository;
import br.com.blogsanapi.repository.UserRepository;

@IntegrationTest
public class PublicationControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private JacksonTester<PublicationRequestDTO> publicationRequestDTOJson;
    @Autowired
    private JacksonTester<PublicationUpdateDTO> publicationUpdateDTOJson;
    @Autowired
    private JacksonTester<PublicationResponseDTO> publicationResponseDTOJson;
    
    @Autowired
    private JacksonTester<CommentRequestDTO> commentRequestDTOJson;
    @Autowired
    private JacksonTester<CommentResponseDTO> commentResponseDTOJson;
    
    @Autowired
    private JacksonTester<AuthenticationDTO> authenticationDTOJson;
    
	@Autowired 
	UserRepository userRepository;
	@Autowired 
	PublicationRepository publicationRepository;
	@Autowired 
	CommentRepository commentRepository;
	@Autowired 
	BCryptPasswordEncoder encoder;
	
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
            .andReturn().getResponse();

        return getTokenValue(response.getContentAsString());
    }

    @Test
    @DisplayName("Create Publication Test - Should return new publication details")
    void createPublicationTest() throws Exception {
        // arrange
        var requestBody = new PublicationRequestDTO("Testing", "imageLink");

        // act
        var result = mvc.perform(
                post("/publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(publicationRequestDTOJson.write(requestBody).getJson())
                        .header("Authorization", "Bearer " + makeLoginAndGetToken())

        ).andReturn().getResponse();

        var responseBody = publicationResponseDTOJson.parseObject(result.getContentAsString());

        // assert
        Assertions.assertNotNull(responseBody.publicationId(), "The publication ID should not be null");
        Assertions.assertEquals(requestBody.description(), responseBody.description(), "The description should match");
    }
    
    @Test
    @DisplayName("Update Publication Test - Should update publication details")
    void updatePublicationTest() throws Exception {
        // arrange
        var requestBody = new PublicationUpdateDTO("description updated");

        // act
        var result = mvc.perform(
                patch("/publications/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(publicationUpdateDTOJson.write(requestBody).getJson())
                        .header("Authorization", "Bearer " + makeLoginAndGetToken())

        ).andReturn().getResponse();

        var responseBody = publicationResponseDTOJson.parseObject(result.getContentAsString());

        // assert
        Assertions.assertNotNull(responseBody.publicationId(), "The publication ID should not be null");
        Assertions.assertEquals(requestBody.description(), responseBody.description(), "The description should match");
    }

    @Test
    @DisplayName("Delete Publication Test - Should return NO_CONTENT status")
    void deletePublicationTest() throws Exception {
        // act
        var result = mvc.perform(
                delete("/publications/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + makeLoginAndGetToken())

        ).andReturn().getResponse();

        // assert
        Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), result.getStatus(), "The status should be 204 NO_CONTENT");
    }

    @Test
    @DisplayName("Create Comment Test - Should return new comment details")
    void createCommentTest() throws Exception {
        // arrange
        var requestBody = new CommentRequestDTO("Testing");

        // act
        var result = mvc.perform(
                post("/publications/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentRequestDTOJson.write(requestBody).getJson())
                        .header("Authorization", "Bearer " + makeLoginAndGetToken())

        ).andReturn().getResponse();

        var responseBody = commentResponseDTOJson.parseObject(result.getContentAsString());

        // assert
        Assertions.assertNull(responseBody.parentCommentId(), "The parent comment ID should be null");
        Assertions.assertFalse(responseBody.edited(), "The comment should not be edited");
        Assertions.assertEquals(requestBody.text(), responseBody.text(), "The text should match");

        Assertions.assertNotNull(responseBody.commentId(), "The comment ID should not be null");
        Assertions.assertNotNull(responseBody.nameUser(), "The user name should not be null");
        Assertions.assertNotNull(responseBody.date(), "The date should not be null");
    }

    @Test
    @DisplayName("Create Reply Test - Should return new reply details")
    void createReplyTest() throws Exception {
        // arrange
        var requestBody = new CommentRequestDTO("Testing");

        // act
        var result = mvc.perform(
                post("/publications/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentRequestDTOJson.write(requestBody).getJson())
                        .header("Authorization", "Bearer " + makeLoginAndGetToken())

        ).andReturn().getResponse();

        var responseBody = commentResponseDTOJson.parseObject(result.getContentAsString());

        // assert
        Assertions.assertFalse(responseBody.edited(), "The reply should not be edited");
        Assertions.assertEquals(requestBody.text(), responseBody.text(), "The text should match");

        Assertions.assertNotNull(responseBody.commentId(), "The comment ID should not be null");
        Assertions.assertNotNull(responseBody.nameUser(), "The user name should not be null");
        Assertions.assertNotNull(responseBody.date(), "The date should not be null");
        Assertions.assertNotNull(responseBody.parentCommentId(), "The parent comment ID should not be null");
    }

    @Test
    @DisplayName("Update Comment Test - Should update comment details")
    void updateCommentTest() throws Exception {
        // arrange
        var requestBody = new CommentRequestDTO("description updated");

        // act
        var result = mvc.perform(
                patch("/publications/comments/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentRequestDTOJson.write(requestBody).getJson())
                        .header("Authorization", "Bearer " + makeLoginAndGetToken())

        ).andReturn().getResponse();

        var responseBody = commentResponseDTOJson.parseObject(result.getContentAsString());

        // assert
        Assertions.assertTrue(responseBody.edited(), "The comment should be edited");
        Assertions.assertEquals(requestBody.text(), responseBody.text(), "The text should match");
    }

    @Test
    @DisplayName("Delete Comment Test - Should return NO_CONTENT status")
    void deleteCommentTest() throws Exception {
        // act
        var result = mvc.perform(
                delete("/publications/comments/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + makeLoginAndGetToken())

        ).andReturn().getResponse();

        // assert
        Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), result.getStatus(), "The status should be 204 NO_CONTENT");
    }
}