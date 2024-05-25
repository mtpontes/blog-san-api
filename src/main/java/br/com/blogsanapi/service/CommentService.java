package br.com.blogsanapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.comment.request.CommentRequestDTO;
import br.com.blogsanapi.model.comment.request.CommentUpdateDTO;
import br.com.blogsanapi.model.comment.response.CommentResponseDTO;
import br.com.blogsanapi.model.publication.Publication;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.repository.CommentRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
public class CommentService {
	
	@Autowired
	private CommentRepository commentRepository;

	
	@Transactional
	public CommentResponseDTO createComment(Long publicationId, CommentRequestDTO dto) {
		User user = this.getUser();
		
		Publication publication = new Publication();
		publication.setId(publicationId);
		
		Comment comment = new Comment(dto.text(), user, publication, null);
		commentRepository.save(comment);
		
		return new CommentResponseDTO(comment);
	}
	
	@Transactional
	public CommentResponseDTO replyComment(Long targetCommentId, CommentRequestDTO dto) {
		Comment commentPrincipal = commentRepository.getReferenceById(targetCommentId);
		Comment comment = new Comment(dto.text(), this.getUser(), null, commentPrincipal.getCommentReference());

		return new CommentResponseDTO(commentRepository.save(comment));
	}
	
	public Page<CommentResponseDTO> getRepliesByComment(Pageable pageable, Long id) {
		return commentRepository.findAllReplies(pageable, id).map(CommentResponseDTO::new);
	}
	public Page<CommentResponseDTO> getCommentsByUser(Pageable pageable, Long id) {
		return commentRepository.findAllByUserId(pageable, id).map(CommentResponseDTO::new);
	}
	public Page<CommentResponseDTO> getCommentsByPublicationId(Pageable pageable, Long id) {
		return commentRepository.findAllByPublicationId(pageable, id).map(CommentResponseDTO::new);
	}
	
	@Transactional
	public CommentResponseDTO updateComment(Long commentId, CommentUpdateDTO dto) {
		Comment comment = commentRepository.findByIdAndUserId(commentId, this.getUser().getId())
				.orElseThrow(EntityNotFoundException::new);
		
		comment.updateText(dto.text());
		return new CommentResponseDTO(commentRepository.save(comment));
	}
	
	@Transactional
	public void deleteComment(Long id) {
		User user = this.getUser();
		commentRepository.deleteByUserIdAndId(user.getId(), id);
	}
	
	private User getUser() {
		return (User) SecurityContextHolder
				.getContext()
				.getAuthentication()
				.getPrincipal();
	}
}