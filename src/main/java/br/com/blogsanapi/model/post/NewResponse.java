package br.com.blogsanapi.model.post;

import java.util.List;

import br.com.blogsanapi.model.comment.CommentResponseDTO;

public record NewResponse(
		Long publicationId,
		Long userId,
		String nameUser,
		String description,
		String imageLink,
		List<CommentResponseDTO> comments
		) {
	
	public NewResponse(Publication p) {
		this(p.getId(), p.getUser().getId(), p.getUser().getName(), p.getDescription(), p.getImageLink(), 
				p.getComments().stream()
				.map(c -> new CommentResponseDTO(c)).toList());
	}
}
