package br.com.blogsanapi.unit.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.publication.Publication;
import br.com.blogsanapi.model.publication.request.PublicationRequestDTO;
import br.com.blogsanapi.model.publication.request.PublicationUpdateDTO;
import br.com.blogsanapi.model.publication.response.PublicationResponseDTO;
import br.com.blogsanapi.model.publication.response.PublicationResponseWithCommentsDTO;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.model.user.UserRole;
import br.com.blogsanapi.repository.CommentRepository;
import br.com.blogsanapi.repository.PublicationRepository;
import br.com.blogsanapi.service.PublicationService;

@ExtendWith(MockitoExtension.class)
class PublicationServiceTest {

	@Mock
	private PublicationRepository pRepository;
	@Mock
	private CommentRepository cRepository;
	@Mock
	private SecurityContext securityContext;
	@Mock
	private Authentication authentication;

	@InjectMocks
	private PublicationService service;

	@Captor
	private ArgumentCaptor<Publication> publicationCaptor;


	private void mockSecurity() {
		SecurityContextHolder.getContext().setAuthentication(authentication);
		User user = new User("root", "root", UserRole.ADMIN, "name", "email");
		when(authentication.getPrincipal()).thenReturn(user);
	}

	@Test
	@DisplayName("Must capture a Publication with the same values as the DTO")
	void createPublicationTest() {
		// arrange
		this.mockSecurity();
		PublicationRequestDTO dto = new PublicationRequestDTO("description", "imageLink");
		
		// act
		service.createPublication(dto);
		
		// assert
		verify(pRepository).save(publicationCaptor.capture());
		Publication publi = publicationCaptor.getValue();
		
		Assertions.assertEquals(dto.description(), publi.getDescription(), "The description of the publication does not match the DTO");
		Assertions.assertEquals(dto.imageLink(), publi.getImageLink(), "The image link of the publication does not match the DTO");
	}


	@Test
	@DisplayName("Must return a publication with comments")
	void getAPublicationWhithCommentsTest() {
		// arrange
		Publication publi = new Publication("description", "imagelink", new User());
		Comment comment0 = new Comment("text-1", new User(), new Publication(), new Comment());
		Comment comment1 = new Comment("text-2", new User(), new Publication(), new Comment());
		Comment comment2 = new Comment("text-3", new User(), new Publication(), new Comment());
		publi.addComment(comment0);
		publi.addComment(comment1);
		publi.addComment(comment2);
		
		when(pRepository.findById(anyLong())).thenReturn(Optional.of(publi));
		
		when(cRepository.findAllByPublicationId(any(), anyLong()))
			.thenReturn(new PageImpl<>(List.of(comment0, comment1, comment2)));
		
		// act
		PublicationResponseWithCommentsDTO result = service.getPublicationWithComments(PageRequest.of(0, 10), 1L);
		
		// assert
		
		Assertions.assertEquals(3, result.comments().size(), "The number of comments should be 3");
		Assertions.assertEquals(comment0.getText(), result.comments().get(0).text(), "The first comment text should match");
		Assertions.assertEquals(comment1.getText(), result.comments().get(1).text(), "The second comment text should match");
		Assertions.assertEquals(comment2.getText(), result.comments().get(2).text(), "The third comment text should match");
		Assertions.assertEquals("description", result.description(), "The description should match the original");
	}

	@Test
	@DisplayName("Must return all publications with the correct data")
	void getAllPublicationsTest() {
		// arrange
		Publication publi = new Publication("description", "imagelink", new User());
		when(pRepository.findAllByParams(any(), any(), any())).thenReturn(new PageImpl<>(List.of(publi)));
		
		// act
		List<PublicationResponseDTO> result = service
			.getAllPublications(PageRequest.of(0, 1), LocalDate.now(), 1L)
			.getContent();
		
		// assert
		Assertions.assertEquals(result.get(0).description(), publi.getDescription(), "The description should match");
		Assertions.assertEquals(result.get(0).imageLink(), publi.getImageLink(), "The imageLik should match");
	}

	@Test
	@DisplayName("Must return user publications with the correct data")
	void getAllPublicationsByUserTest() {
		// arrange
		Publication publi = new Publication("description", "imagelink", new User());
		when(pRepository.findAllByUserId(any(), any())).thenReturn(new PageImpl<>(List.of(publi)));
		
		// act
		List<PublicationResponseDTO> result = service.getAllPublicationsByUser(PageRequest.of(0, 1), 1L)
			.getContent();
		
		// assert
		Assertions.assertEquals(result.get(0).description(), publi.getDescription(), "The description should match");
		Assertions.assertEquals(result.get(0).imageLink(), publi.getImageLink(), "The imageLink should match");
	}

	@Test
	@DisplayName("Must return updated description")
	void updatePublicationTest() {
		// arrange
		this.mockSecurity();
		Publication publi = new Publication("description", "imagelink", new User());
		PublicationUpdateDTO dto = new PublicationUpdateDTO("description-update");
		when(pRepository.findByIdAndUserId(any(), any())).thenReturn(Optional.of(publi));
		
		// act
		PublicationResponseDTO result = service.updatePublication(1L, dto);
		
		// assert
		Assertions.assertEquals(result.description(), dto.description(), "The description should be updated");
	}
}