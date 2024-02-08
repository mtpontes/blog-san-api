package br.com.blogsanapi.model.publication;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.blogsanapi.model.comment.CommentResponseDTO;

public record PublicationResponseDTO(
		Long publicationId,
		Long userId,
		String nameUser,
		String description,
		String imageLink,
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
		LocalDateTime date,
		List<CommentResponseDTO> comments
		) {
	
	public PublicationResponseDTO(Publication p) {
		this(
				p.getId(), 
				p.getUser().getId(), 
				p.getUser().getName(), 
				p.getDescription(), 
				p.getImageLink(), 
				p.getDate(), 
				p.getComments().stream().map(CommentResponseDTO::new).toList()
			);
	}
}
