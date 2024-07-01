package br.com.blogsanapi.unit.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import br.com.blogsanapi.configs.ControllerUnitTest;
import br.com.blogsanapi.controller.PublicationController;
import br.com.blogsanapi.infra.security.TokenService;
import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.comment.request.CommentRequestDTO;
import br.com.blogsanapi.model.comment.response.CommentResponseDTO;
import br.com.blogsanapi.model.publication.Publication;
import br.com.blogsanapi.model.publication.request.PublicationRequestDTO;
import br.com.blogsanapi.model.publication.request.PublicationUpdateDTO;
import br.com.blogsanapi.model.publication.response.PublicationResponseDTO;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.model.user.UserRole;
import br.com.blogsanapi.repository.UserRepository;
import br.com.blogsanapi.service.CommentService;
import br.com.blogsanapi.service.PublicationService;
import br.com.blogsanapi.utils.ControllerTestUtils;

@ControllerUnitTest
@WebMvcTest(PublicationController.class)
public class PublicationControllerTest {

    private final String BASE_URL = "/publications";

    @MockBean
    private PublicationService publicationService;
    @MockBean
    private CommentService commentService;

    // security filter dependencies
    @MockBean
    private TokenService tokenService;
    @MockBean
    private UserRepository userRepository;

    @Autowired
    private MockMvc mvc;

    // Mocked User
    private final User userMock = User.builder()
        .id(1L)
        .login("loginDefault")
        .password("passwordDefault")
        .role(UserRole.ADMIN)
        .name("Name Default")
        .email("default@email.com")
        .build();

    // Mocked Publication and Date
    private final LocalDateTime date = LocalDateTime.now().plusDays(1L);
    private final Publication publicationMock = Publication.builder()
        .id(1L)
        .description("default")
        .imageLink("defaultLink.com")
        .date(date)
        .edited(false)
        .user(userMock)
        .comments(null)
        .build();

    // Mocked Comments
    private final Comment commentPublicationMock = Comment.builder()
        .id(1L)
        .text("textDefault")
        .date(LocalDateTime.now().plusDays(2L))
        .edited(false)
        .user(userMock)
        .publication(publicationMock)
        .parentComment(null)
        .replies(null)
        .build();

    private final Comment replyCommentMock = Comment.builder()
        .id(1L)
        .text("textDefault")
        .date(LocalDateTime.now().plusDays(2L))
        .edited(false)
        .user(userMock)
        .publication(null)
        .parentComment(commentPublicationMock)
        .replies(null)
        .build();

    // Response DTOs
    private final PublicationResponseDTO publicationResponseDTO = new PublicationResponseDTO(publicationMock);
    private final CommentResponseDTO commentResponseDTO = new CommentResponseDTO(commentPublicationMock);
    private final CommentResponseDTO replyResponseDTO = new CommentResponseDTO(replyCommentMock);

    // JSON Testers
    @Autowired
    private JacksonTester<PublicationRequestDTO> publicationRequestDTOJson;
    @Autowired
    private JacksonTester<CommentRequestDTO> commentRequestDTOJson;
    @Autowired
    private JacksonTester<PublicationUpdateDTO> publicationUpdateDTOJson;


    @Test
    @DisplayName("Unit - Create Publication - Should return status 200")
    @WithMockUser(roles = {"ADMIN"})
    void createPublicationTest01() throws Exception {
        // arrange
        var createPubli = new PublicationRequestDTO("description", "imageLink");
        String requestBody = publicationRequestDTOJson.write(createPubli).getJson();
        when(publicationService.createPublication(any())).thenReturn(publicationResponseDTO);

        // act
        ControllerTestUtils.postRequest(mvc, this.BASE_URL, requestBody)
            // assert
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Unit - Update Publication 01 - Should return status 200")
    @WithMockUser(roles = {"ADMIN"})
    void updatePublicationTest01() throws Exception {
        // arrange
        var update = new PublicationUpdateDTO("update");
        String requestBody = publicationUpdateDTOJson.write(update).getJson();
        when(publicationService.updatePublication(any(), any())).thenReturn(publicationResponseDTO);

        // act
        ControllerTestUtils.patchRequest(this.mvc, (this.BASE_URL + "/1"), requestBody)
            // assert
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Unit - Delete Publication 01 - Should return status 204")
    @WithMockUser(roles = {"ADMIN"})
    void deletePublicationTest01() throws Exception {
        // act
        mvc.perform(
            delete("/publications/1")
                .contentType(MediaType.APPLICATION_JSON)
            )
            // assert
            .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Unit - Create Comment 01 - Should return status 201")
    @WithMockUser(roles = {"CLIENT"})
    void createCommentTest01() throws Exception {
        // arrange
        var createComment = new CommentRequestDTO("text comment");
        String requestBody = commentRequestDTOJson.write(createComment).getJson();
        when(commentService.createComment(anyLong(), any())).thenReturn(commentResponseDTO);

        // act
        ControllerTestUtils.postRequest(mvc, (this.BASE_URL + "/1/comments"), requestBody)
            // assert
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Unit - Reply to Comment - Should return status 201")
    @WithMockUser(roles = {"CLIENT"})
    void replyCommentTest01() throws Exception {
        // arrange
        var createComment = new CommentRequestDTO("text comment");
        String requestBody = commentRequestDTOJson.write(createComment).getJson();
        when(commentService.replyComment(anyLong(), any())).thenReturn(replyResponseDTO);

        // act
        ControllerTestUtils.postRequest(mvc, (this.BASE_URL + "/comments/1"), requestBody)
            // assert
            .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Unit - Update Comment 01 - Should return status 200")
    @WithMockUser(roles = {"CLIENT"})
    void updateCommentTest01() throws Exception {
        // arrange
        var update = new CommentRequestDTO("update");
        String requestBody = commentRequestDTOJson.write(update).getJson();
        when(commentService.updateComment(any(), any())).thenReturn(commentResponseDTO);

        // act
        ControllerTestUtils.patchRequest(mvc, (BASE_URL + "/comments/1"), requestBody)
            // assert
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Unit - Delete Comment - Should return status 204")
    @WithMockUser(roles = "CLIENT")
    void deleteCommentTest01() throws Exception {
        // act
        mvc.perform(
            delete("/publications/comments/1")
                .contentType(MediaType.APPLICATION_JSON)
        )
        // assert
        .andExpect(status().isNoContent());
    }
}