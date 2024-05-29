package br.com.blogsanapi.integration.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles(profiles = "test")
@PropertySource("classpath:application-test.properties")
@DirtiesContext(classMode = ClassMode.BEFORE_EACH_TEST_METHOD)
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
    private String token;
    
	@Autowired 
	UserRepository userRepository;
	@Autowired 
	PublicationRepository publicationRepository;
	@Autowired 
	CommentRepository commentRepository;
	@Autowired 
	BCryptPasswordEncoder encoder;
	
    @BeforeEach
    void setup() throws Exception {
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
    	
        var response = mvc.perform(
        		post("/auth/login")
        			.contentType(MediaType.APPLICATION_JSON)
        			.content(authenticationDTOJson.write(new AuthenticationDTO("test", "test")).getJson())
        		)
        		.andReturn().getResponse();
        
        this.token = response.getContentAsString()
        				.split(":")[1]
        				.replace("\"", "")
        				.replace("}", "");
    }

    @Test
    void createPublicationTest() throws Exception {
        // arrange
        var requestBody = new PublicationRequestDTO("Testing", "imageLink");
        
        // act
        var result = mvc.perform(
                post("/publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(publicationRequestDTOJson.write(requestBody).getJson())
                        .header("Authorization", "Bearer " + this.token)
                        
        ).andReturn().getResponse();

        var responseBody = publicationResponseDTOJson.parseObject(result.getContentAsString());

        // assert
        Assertions.assertNotNull(responseBody.publicationId());
        Assertions.assertEquals(requestBody.description(), responseBody.description());
    }
    
    @Test
    void updatePublicationTest() throws Exception {
    	// arrange
    	var requestBody = new PublicationUpdateDTO("description updated");
    	
    	// act
    	var result = mvc.perform(
    			patch("/publications/1")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(publicationUpdateDTOJson.write(requestBody).getJson())
    			.header("Authorization", "Bearer " + this.token)
    			
    			).andReturn().getResponse();
    	
    	var responseBody = publicationResponseDTOJson.parseObject(result.getContentAsString());
    	
    	// assert
    	Assertions.assertNotNull(responseBody.publicationId());
    	Assertions.assertEquals(requestBody.description(), responseBody.description());
    }
    
    @Test
    void deletePublicationTest() throws Exception {
    	// act
    	var result = mvc.perform(
    			delete("/publications/1")
    			.contentType(MediaType.APPLICATION_JSON)
    			.header("Authorization", "Bearer " + this.token)
    			
    			).andReturn().getResponse();
    	
    	// assert
    	Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), result.getStatus());
    }
    
    @Test
    void createCommentTest() throws Exception {
        // arrange
        var requestBody = new CommentRequestDTO("Testing");
        
        // act
        var result = mvc.perform(
                post("/publications/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentRequestDTOJson.write(requestBody).getJson())
                        .header("Authorization", "Bearer " + this.token)
                        
        ).andReturn().getResponse();

        var responseBody = commentResponseDTOJson.parseObject(result.getContentAsString());

        // assert
        Assertions.assertNull(responseBody.parentCommentId());
        Assertions.assertFalse(responseBody.edited());
        Assertions.assertEquals(requestBody.text(), responseBody.text());
        
        Assertions.assertNotNull(responseBody.commentId());
        Assertions.assertNotNull(responseBody.nameUser());
        Assertions.assertNotNull(responseBody.date());
    }
    
    @Test
    void createReplyTest() throws Exception {
    	// arrange
    	var requestBody = new CommentRequestDTO("Testing");
    	
    	// act
    	var result = mvc.perform(
    			post("/publications/comments/1")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(commentRequestDTOJson.write(requestBody).getJson())
    			.header("Authorization", "Bearer " + this.token)
    			
    			).andReturn().getResponse();
    	
    	var responseBody = commentResponseDTOJson.parseObject(result.getContentAsString());
    	
    	// assert
    	Assertions.assertFalse(responseBody.edited());
    	Assertions.assertEquals(requestBody.text(), responseBody.text());

    	Assertions.assertNotNull(responseBody.commentId());
    	Assertions.assertNotNull(responseBody.nameUser());
    	Assertions.assertNotNull(responseBody.date());
    	Assertions.assertNotNull(responseBody.parentCommentId());
    }
    
    @Test
    void updateCommentTest() throws Exception {
    	// arrange
    	var requestBody = new CommentRequestDTO("description updated");
    	
    	// act
    	var result = mvc.perform(
    			patch("/publications/comments/1")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(commentRequestDTOJson.write(requestBody).getJson())
    			.header("Authorization", "Bearer " + this.token)
    			
    			).andReturn().getResponse();
    	
    	var responseBody = commentResponseDTOJson.parseObject(result.getContentAsString());
    	
    	// assert
    	Assertions.assertTrue(responseBody.edited());
    	Assertions.assertEquals(requestBody.text(), responseBody.text());
    }

    @Test
    void deleteCommentTest() throws Exception {
    	// act
    	var result = mvc.perform(
    			delete("/publications/comments/1")
    			.contentType(MediaType.APPLICATION_JSON)
    			.header("Authorization", "Bearer " + this.token)
    			
    			).andReturn().getResponse();
    	
    	// assert
    	Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), result.getStatus());
    }
}