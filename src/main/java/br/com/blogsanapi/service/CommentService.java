package br.com.blogsanapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.comment.CommentRequestDTO;
import br.com.blogsanapi.model.comment.CommentResponseDTO;
import br.com.blogsanapi.model.comment.CommentUpdateDTO;
import br.com.blogsanapi.model.post.Publication;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.repository.CommentRepository;
import br.com.blogsanapi.repository.PublicationRepository;

public class CommentService {
	
	@Autowired
	private CommentRepository commentRepository;
	@Autowired
	private PublicationRepository publicationRepository;
	
	
	public CommentResponseDTO createComment(CommentRequestDTO dto) {
		Publication publi = publicationRepository.findById(dto.publicationId()).get();
		User user = this.getUser();
		Comment comment = new Comment(dto.text(), user, publi);
		
		return new CommentResponseDTO(comment);
	}
	
	public CommentResponseDTO updateComment(CommentUpdateDTO dto) {
		Comment comment = commentRepository.getReferenceById(dto.id());
		comment.updateText(dto.text());
		
		return new CommentResponseDTO(comment);
	}
	
	public CommentResponseDTO getComment(Long id) {
		return new CommentResponseDTO(commentRepository.getReferenceById(id));
	}
	
	public void deleteComment(Long id) {
		commentRepository.deleteById(id);
	}


	private User getUser() {
		return (User) SecurityContextHolder
				.getContext()
				.getAuthentication()
				.getPrincipal();
	}
}