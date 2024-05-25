package br.com.blogsanapi.model.publication.response;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.blogsanapi.model.comment.response.CommentResponseDTO;
import br.com.blogsanapi.model.publication.Publication;

public record PublicationResponseWithCommentsDTO(
		Long publicationId,
		Long userId,
		String nameUser,
		String description,
		String imageLink,
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
		LocalDateTime date,
		List<CommentResponseDTO> comments
		) {
	
	public PublicationResponseWithCommentsDTO(Publication p) {
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