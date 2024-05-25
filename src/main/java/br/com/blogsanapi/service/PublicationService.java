package br.com.blogsanapi.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.comment.response.CommentResponseDTO;
import br.com.blogsanapi.model.publication.Publication;
import br.com.blogsanapi.model.publication.request.PublicationRequestDTO;
import br.com.blogsanapi.model.publication.request.PublicationUpdateRequestDTO;
import br.com.blogsanapi.model.publication.response.PublicationResponseDTO;
import br.com.blogsanapi.model.publication.response.PublicationResponseWithCommentsDTO;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.repository.CommentRepository;
import br.com.blogsanapi.repository.PublicationRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.NoResultException;

@Service
public class PublicationService {

	@Autowired
	private PublicationRepository publicationRepository;
	@Autowired
	private CommentRepository commentRepository;
	
	
	public PublicationResponseDTO createPublication(PublicationRequestDTO publication) {
		Publication publi = new Publication(publication.description(), publication.imageLink(), this.getUser());
		publicationRepository.save(publi);
		
		return new PublicationResponseDTO(publi);
	}
	
	public PublicationResponseWithCommentsDTO getAPublicationWhithComments(Pageable pageable, Long publicationId) {
		Publication publication = publicationRepository.findById(publicationId)
				.orElseThrow(EntityNotFoundException::new);
		
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

	public Page<PublicationResponseDTO> getAllPublications(Pageable pageable, LocalDate date, Long userId) {
		return publicationRepository.findAllByParams(pageable, date, userId)
				.map(PublicationResponseDTO::new);
	}
	public Page<PublicationResponseDTO> getAllPublicationsByUser(Pageable pageable, Long id) {
		return publicationRepository.findAllByUserId(pageable, id).map(PublicationResponseDTO::new);
	}
	
	public PublicationResponseDTO updatePublication(Long publicationId, PublicationUpdateRequestDTO dto) {
		Publication publi = publicationRepository.findByIdAndUserId(publicationId, this.getUser().getId())
				.orElseThrow(EntityNotFoundException::new);
		
		publi.updateDescription(dto.description());
		publicationRepository.save(publi);
		
		return new PublicationResponseDTO(publi);
	}
	
	public void deletePublication(Long id) {
		User user = this.getUser();
		publicationRepository.deleteByUserIdAndId(user.getId(), id);
	}
	
	private User getUser() {
		return (User) SecurityContextHolder
				.getContext()
				.getAuthentication()
				.getPrincipal();
	}
}