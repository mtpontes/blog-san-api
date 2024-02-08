package br.com.blogsanapi.model.publication;

import jakarta.validation.constraints.AssertTrue;

public record PublicationRequestDTO(
		String description,
		String imageLink) {
	
	@AssertTrue
	boolean notBlankPost() {
		return description != null && !description.isBlank() ||
				imageLink != null && !imageLink.isBlank();
	}
}
