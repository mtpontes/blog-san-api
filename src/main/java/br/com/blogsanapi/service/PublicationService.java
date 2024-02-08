package br.com.blogsanapi.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.comment.response.CommentResponseDTO;
import br.com.blogsanapi.model.publication.Publication;
import br.com.blogsanapi.model.publication.request.PublicationDateRequestDTO;
import br.com.blogsanapi.model.publication.request.PublicationRequestDTO;
import br.com.blogsanapi.model.publication.request.PublicationUpdateRequestDTO;
import br.com.blogsanapi.model.publication.response.PublicationResponseDTO;
import br.com.blogsanapi.model.publication.response.PublicationResponseWithCommentsDTO;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.repository.CommentRepository;
import br.com.blogsanapi.repository.PublicationRepository;
import jakarta.persistence.NoResultException;

@Service
public class PublicationService {
//	private static Logger logger = LoggerFactory.getLogger(PublicationService.class);

	@Autowired
	private PublicationRepository publicationRepository;
	@Autowired
	private CommentRepository commentRepository;
	
	
	public PublicationResponseDTO createPublication(PublicationRequestDTO publication) {
		User user = this.getUser();
		this.accesRoleVerify(user);
		
		Publication publi = new Publication(
				publication.description(), publication.imageLink(), user);
		publicationRepository.save(publi);
		
		return new PublicationResponseDTO(publi);
	}
	
	public PublicationResponseWithCommentsDTO getAPublicationWhithComments(Pageable pageable, Long publicationId) {
		Publication publication = publicationRepository.findById(publicationId).orElse(null);
		if (publication == null) throw new NoResultException("Publication not found");
		
		Page<Comment> commentsPage = commentRepository.findAllByPublicationId(pageable, publicationId);
		List<CommentResponseDTO> commentResponse = commentsPage.getContent().stream()
				.map(CommentResponseDTO::new)
				.collect(Collectors.toList());
		
		return new PublicationResponseWithCommentsDTO(
				publication.getId(),
				publication.getUser().getId(), 
				publication.getUser().getName(), 
				publication.getDescription(), 
				publication.getImageLink(), 
				publication.getDate(), 
				commentResponse
		);
	}

	public Page<PublicationResponseDTO> getAllPublications(Pageable pageable) {
		return publicationRepository.findAll(pageable).map(PublicationResponseDTO::new);
	}
	public Page<PublicationResponseDTO> getAllPublicationsByDate(Pageable pageable, PublicationDateRequestDTO dto) {
		return publicationRepository.findAllByDate(pageable, dto.date()).map(PublicationResponseDTO::new);
	}
	public Page<PublicationResponseDTO> getAllPublicationsByUser(Pageable pageable, Long id) {
		return publicationRepository.findAllByUserId(pageable, id).map(PublicationResponseDTO::new);
	}
	
	/**
	 * Updates the publication with the data provided
	 * 
	 * @param dto DTO with data for update
	 * @throws IllegalArgumentException If both atributes `description` and `imageLink` are null
	 */
	public PublicationResponseDTO updatePublication(PublicationUpdateRequestDTO dto) {
		Publication publi = publicationRepository.getReferenceById(dto.id());
		User user = this.getUser();
		this.accesVerify(publi);
		this.accesRoleVerify(user);
		
		if (dto.description() == null && publi.getImageLink() == null) {
			throw new IllegalArgumentException("Both description and imgeLink cannot be null");
		}
		
		publi.updateDescription(dto.description());
		publicationRepository.save(publi);
		
		return new PublicationResponseDTO(publi);
	}
	
	public void deletePublication(Long id) {
		User user = this.getUser();
		this.accesRoleVerify(user);
		publicationRepository.deleteByUserIdAndId(user.getId(), id);
	}
	
	private User getUser() {
		return (User) SecurityContextHolder
				.getContext()
				.getAuthentication()
				.getPrincipal();
	}
	private void accesVerify(Publication publi) throws AccessDeniedException {
		User userByToken = this.getUser();
		User userByPubli = publi.getUser();
		if (userByToken == null || !userByPubli.getId().equals(userByToken.getId())) 
			throw new AccessDeniedException("User do not have permission for access this resource");
	}
	private void accesRoleVerify(User user) throws AccessDeniedException {
		if (user == null || user.getAuthorities().contains(new SimpleGrantedAuthority("ADMIN"))) 
			throw new AccessDeniedException("User do not have permission for access this resource");
	}
}
