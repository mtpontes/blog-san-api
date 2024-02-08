package br.com.blogsanapi.model.publication.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.blogsanapi.model.publication.Publication;

public record PublicationResponseDTO(
		Long publicationId,
		Long userId,
		String nameUser,
		String description,
		String imageLink,
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
		LocalDateTime date
		) {
	
	public PublicationResponseDTO(Publication p) {
		this(
				p.getId(), 
				p.getUser().getId(), 
				p.getUser().getName(), 
				p.getDescription(), 
				p.getImageLink(), 
				p.getDate() 
			);
	}
}
