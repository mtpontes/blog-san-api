package br.com.blogsanapi.model.publication;

import jakarta.validation.constraints.NotNull;

public record PublicationUpdateRequestDTO(
		@NotNull
		Long id,
		String description) {
}
