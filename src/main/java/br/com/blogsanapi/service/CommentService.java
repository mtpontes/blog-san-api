package br.com.blogsanapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.comment.request.CommentRepliRequestDTO;
import br.com.blogsanapi.model.comment.request.CommentRequestDTO;
import br.com.blogsanapi.model.comment.request.CommentUpdateDTO;
import br.com.blogsanapi.model.comment.response.CommentResponseDTO;
import br.com.blogsanapi.model.publication.Publication;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.repository.CommentRepository;
import br.com.blogsanapi.repository.PublicationRepository;

@Service
public class CommentService {
	private static Logger logger = LoggerFactory.getLogger(PublicationService.class);
	
	@Autowired
	private CommentRepository commentRepository;

	
	public CommentResponseDTO createComment(CommentRequestDTO dto) {
		User user = this.getUser();
		
		Publication publication = new Publication();
		publication.setId(dto.publicationId());
		
		Comment comment = new Comment(dto.text(), user, publication);
		commentRepository.save(comment);
		
		return new CommentResponseDTO(comment);
	}
	public CommentResponseDTO replyComment(CommentRepliRequestDTO dto) {
		User user = this.getUser();
		Comment commentPrincipal = commentRepository.getReferenceById(dto.targetCommentId());
		
		Comment comment;
		if (commentPrincipal.getParentComment() == null) {
			comment = new Comment(dto.text(), user, commentPrincipal);
		} else {
			comment = new Comment(dto.text(), user, commentPrincipal.getParentComment());
		}
		commentRepository.save(comment);
		
		return new CommentResponseDTO(comment);
	}
	public Page<CommentResponseDTO> getRepliesByComment(Pageable pageable, Long id) {
		return commentRepository.findAllReplies(pageable, id).map(CommentResponseDTO::new);
	}
	
	public Page<Comment> getCommentsByUser(Pageable pageable, Long id) {
		return commentRepository.findAllByUserId(pageable, id);
	}
	public Page<Comment> getCommentsByPublicationId(Pageable pageable, Long id) {
		return commentRepository.findAllByPublicationId(pageable, id);
	}
	
	public CommentResponseDTO updateComment(CommentUpdateDTO dto) {
		Comment comment = commentRepository.getReferenceById(dto.id());
		this.accesVerify(comment);
		
		comment.updateText(dto.text());
		commentRepository.flush();
		
		return new CommentResponseDTO(comment);
	}
	
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
	private void accesVerify(Comment comment) throws AccessDeniedException {
		User userByToken = this.getUser();
		User userByComment = comment.getUser();
		if (userByToken == null || !userByComment.getId().equals(userByToken.getId())) 
			throw new AccessDeniedException("User do not have permission for access this resource");
	}

}