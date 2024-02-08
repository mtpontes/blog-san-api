package br.com.blogsanapi.model.post;

import jakarta.validation.constraints.NotNull;

public record PublicationUpdateRequestDTO(
		@NotNull
		Long id,
		String description) {
}
