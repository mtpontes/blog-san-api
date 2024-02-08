package br.com.blogsanapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.comment.CommentRequestDTO;
import br.com.blogsanapi.model.comment.CommentUpdateDTO;
import br.com.blogsanapi.model.publication.Publication;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.repository.CommentRepository;
import br.com.blogsanapi.repository.PublicationRepository;

@Service
public class CommentService {
	
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private PublicationRepository publicationRepository;
	
	
	public Comment createComment(CommentRequestDTO dto) {
		User user = this.getUser();
		Publication publi = publicationRepository.getReferenceById(dto.publicationId());
		Comment comment = new Comment(dto.text(), user, publi);
		commentRepository.save(comment);
		
		return comment;
	}
	
	public Page<Comment> getCommentsByUser(Pageable pageable, Long id) {
		return commentRepository.findAllByUserId(pageable, id);
	}
	
	public Comment updateComment(CommentUpdateDTO dto) {
		Comment comment = commentRepository.getReferenceById(dto.id());
		this.accesVerify(comment);
		comment.updateText(dto.text());
		
		return comment;
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
		if (userByToken == null || !userByComment.getId().equals(userByToken.getId())) throw new AccessDeniedException("User do not have permission for access this task");
	}
}