package br.com.blogsanapi.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.comment.request.CommentRequestDTO;
import br.com.blogsanapi.model.comment.response.CommentResponseDTO;
import br.com.blogsanapi.model.publication.Publication;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.model.user.UserRole;
import br.com.blogsanapi.repository.CommentRepository;
import br.com.blogsanapi.repository.PublicationRepository;
import br.com.blogsanapi.service.CommentService;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

	@Mock
	private PublicationRepository publicationRepository;
	@Mock
	private CommentRepository repository;
	@Mock
	private SecurityContext securityContext;
	@Mock
	private Authentication authentication;

	@InjectMocks
	private CommentService service;

	@Captor
	private ArgumentCaptor<Comment> commentCaptor;


	private void mockSecurity() {
		SecurityContextHolder.getContext().setAuthentication(authentication);
		User user = User.builder()
			.id(1L)
			.login("root")
			.password("root")
			.role(UserRole.ADMIN)
			.name("name")
			.email("email")
			.build();
		when(authentication.getPrincipal()).thenReturn(user);
	}

	@Test
	@DisplayName("Must create comment")
	void createCommentTest() {
		// arrange
		this.mockSecurity();
		Long publicationId = 1l;
		CommentRequestDTO dto = new CommentRequestDTO("text");
		Publication publi = Publication.builder().description("description").imageLink("link").user(new User()).id(1L).build();
		when(publicationRepository.findById(any())).thenReturn(Optional.of(publi));

		
		// act
		service.createComment(publicationId, dto);
		
		// assert
		verify(repository).save(commentCaptor.capture());
		Comment captured = commentCaptor.getValue();

		Assertions.assertNull(captured.getParentComment(), "ParentComment must not be null");
		Assertions.assertEquals(captured.getPublication().getId(), publicationId, "Id must be matched");
		Assertions.assertEquals(captured.getText(), dto.text(), "Text must be matched");
	}

	@Test
	@DisplayName("The parentComment must be the target comment")
	void replyCommentTest01() {
		// arrange
		this.mockSecurity();
		Long targetCommentId = 1l;
		CommentRequestDTO dto = new CommentRequestDTO("reply");
		Comment commentReference = Comment.builder()
			.id(10L)
			.text("commentReference")
			.user(User.builder().id(20L).build())
			.build();
		when(repository.getReferenceById(anyLong())).thenReturn(commentReference);
		when(repository.save(any())).thenReturn(commentReference);
		
		// act
		service.replyComment(targetCommentId, dto);
		
		// assert
		verify(repository).save(commentCaptor.capture());
		Comment replyResult = commentCaptor.getValue();
		
		Assertions.assertNotNull(replyResult.getParentComment(), "ParentComment must not be null");
		Assertions.assertEquals(replyResult.getParentComment().getId(), 10L, "ID must be matched");
	}

	@Test
	@DisplayName("The parentComment must be the same parentComment of the target comment")
	void replyCommentTest02() {
		// arrange
		this.mockSecurity();
		Long targetCommentId = 1l;
		CommentRequestDTO dto = new CommentRequestDTO("reply");
		Comment commentReference = Comment.builder()
			.id(20L)
			.text("commentReference")
			.user(User.builder().id(10L).build())
			.parentComment(Comment.builder()
				.id(1L)
				.text("parentComment")
				.user(User.builder().id(1L).build())
				.build())
			.build();
		when(repository.getReferenceById(anyLong())).thenReturn(commentReference);
		when(repository.save(any())).thenReturn(commentReference);
		
		// act
		service.replyComment(targetCommentId, dto);
		
		// assert
		verify(repository).save(commentCaptor.capture());
		Comment replyResult = commentCaptor.getValue();
		
		Assertions.assertNotNull(replyResult.getParentComment(), "ParenComment must not be null");
		Assertions.assertEquals(replyResult.getParentComment().getId(), 1L, "ID should be matched");
		Assertions.assertEquals(replyResult.getParentComment().getText(), commentReference.getParentComment().getText(), "Text should be matched");
	}

	@Test
	@DisplayName("Must update comment text")
	void updateCommentTest() {
		// arrange
		this.mockSecurity();
		LocalDateTime DATE_COMMENT = LocalDateTime.now().minusMinutes(10);
		Boolean EDITED_COMMENT = false;
		Comment comment = Comment.builder()
			.id(100L)
			.text("text-content")
			.date(DATE_COMMENT)
			.edited(EDITED_COMMENT)
			.user(User.builder().id(200L).build())
			.build();

		Long commentId = 1L;
		CommentRequestDTO update = new CommentRequestDTO("newText");

		when(repository.findByIdAndUserId(any(), any())).thenReturn(Optional.of(comment));
		when(repository.save(any())).thenReturn(comment);
		
		// act
		CommentResponseDTO output = service.updateComment(commentId, update);
		
		// assert
		
		Assertions.assertEquals(output.text(), update.text(), "Text should be matched");
		Assertions.assertNotEquals(output.edited(), EDITED_COMMENT, "Edited must not match");
		Assertions.assertNotEquals(output.date(), DATE_COMMENT, "Date must not match");
	}
}