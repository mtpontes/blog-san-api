package br.com.blogsanapi.integration.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.comment.request.CommentRequestDTO;
import br.com.blogsanapi.model.comment.response.CommentResponseDTO;
import br.com.blogsanapi.model.publication.Publication;
import br.com.blogsanapi.model.publication.request.PublicationRequestDTO;
import br.com.blogsanapi.model.publication.request.PublicationUpdateRequestDTO;
import br.com.blogsanapi.model.publication.response.PublicationResponseDTO;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.model.user.UserRole;
import br.com.blogsanapi.service.CommentService;
import br.com.blogsanapi.service.PublicationService;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@ActiveProfiles(profiles = "test")
public class PublicationControllerTest {

    @MockBean
    private PublicationService publicationService;
    @MockBean
    private CommentService commentService;

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
    private JacksonTester<PublicationRequestDTO> publicationRequestJson;
    @Autowired
    private JacksonTester<CommentRequestDTO> commentRequestJson;
    @Autowired
    private JacksonTester<PublicationUpdateRequestDTO> publicationUpdateJson;

    @Autowired
    private JacksonTester<PublicationResponseDTO> publicationResponseJson;
    @Autowired
    private JacksonTester<CommentResponseDTO> commentResponseJson;

    @Test
    @DisplayName("Create Publication - Should return CREATED status and correct publication ID")
    @WithMockUser(roles = {"ADMIN"})
    void createPublicationTest() throws Exception {
        // arrange
        var createPubli = new PublicationRequestDTO("description", "imageLink");
        when(publicationService.createPublication(any())).thenReturn(publicationResponseDTO);

        // act
        var result = mvc.perform(
                post("/publications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(publicationRequestJson.write(createPubli).getJson())
        ).andReturn().getResponse();

        PublicationResponseDTO responseBody = publicationResponseJson.parseObject(result.getContentAsString());

        // assert
        Assertions.assertEquals(HttpStatus.CREATED.value(), result.getStatus(), "Expected CREATED status");
        Assertions.assertEquals(publicationMock.getId(), responseBody.publicationId(), "Expected publication ID to match");
    }

    @Test
    @DisplayName("Update Publication - Should return OK status")
    @WithMockUser(roles = {"ADMIN", "CLIENT"})
    void updatePublicationTest() throws Exception {
        // act
        var update = new PublicationUpdateRequestDTO("update");
        when(publicationService.updatePublication(any(), any())).thenReturn(publicationResponseDTO);

        var resultRequestWithEmptyJsonBody = mvc.perform(
                patch("/publications/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(publicationUpdateJson.write(update).getJson())
        ).andReturn().getResponse();

        // assert
        Assertions.assertEquals(HttpStatus.OK.value(), resultRequestWithEmptyJsonBody.getStatus(), "Expected OK status");
    }

    @Test
    @DisplayName("Delete Publication - Should return NO_CONTENT status")
    @WithMockUser(roles = {"ADMIN", "CLIENT"})
    void deletePublicationTest() throws Exception {
        // act
        var result = mvc.perform(
                delete("/publications/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // assert
        Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), result.getStatus(), "Expected NO_CONTENT status");
    }

    @Test
    @DisplayName("Create Comment - Should return CREATED status and correct comment ID")
    @WithMockUser(roles = {"CLIENT"})
    void createCommentTest() throws Exception {
        // arrange
        var createComment = new CommentRequestDTO("text comment");
        when(commentService.createComment(anyLong(), any())).thenReturn(commentResponseDTO);

        // act
        var result = mvc.perform(
                post("/publications/1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentRequestJson.write(createComment).getJson())
        ).andReturn().getResponse();

        CommentResponseDTO responseBody = commentResponseJson.parseObject(result.getContentAsString());

        // assert
        Assertions.assertEquals(HttpStatus.CREATED.value(), result.getStatus(), "Expected CREATED status");
        Assertions.assertEquals(commentPublicationMock.getId(), responseBody.commentId(), "Expected comment ID to match");
    }

    @Test
    @DisplayName("Reply to Comment - Should return CREATED status and correct reply comment ID")
    @WithMockUser(roles = {"CLIENT"})
    void replyCommentTest() throws Exception {
        // arrange
        var createComment = new CommentRequestDTO("text comment");
        when(commentService.replyComment(anyLong(), any())).thenReturn(replyResponseDTO);

        // act
        var result = mvc.perform(
                post("/publications/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentRequestJson.write(createComment).getJson())
        ).andReturn().getResponse();

        CommentResponseDTO responseBody = commentResponseJson.parseObject(result.getContentAsString());

        // assert
        Assertions.assertEquals(HttpStatus.CREATED.value(), result.getStatus(), "Expected CREATED status");
        Assertions.assertEquals(replyCommentMock.getId(), responseBody.commentId(), "Expected reply comment ID to match");
    }

    @Test
    @DisplayName("Update Comment - Should return OK status")
    @WithMockUser(roles = {"CLIENT"})
    void updateCommentTest() throws Exception {
        // act
        var update = new CommentRequestDTO("update");
        when(commentService.updateComment(any(), any())).thenReturn(commentResponseDTO);

        var resultRequestWithEmptyJsonBody = mvc.perform(
                patch("/publications/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(commentRequestJson.write(update).getJson())
        ).andReturn().getResponse();

        // assert
        Assertions.assertEquals(HttpStatus.OK.value(), resultRequestWithEmptyJsonBody.getStatus(), "Expected OK status");
    }

    @Test
    @DisplayName("Delete Comment - Should return NO_CONTENT status")
    @WithMockUser(roles = "CLIENT")
    void deleteCommentTest() throws Exception {
        // act
        var result = mvc.perform(
                delete("/publications/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        // assert
        Assertions.assertEquals(HttpStatus.NO_CONTENT.value(), result.getStatus(), "Expected NO_CONTENT status");
    }
}