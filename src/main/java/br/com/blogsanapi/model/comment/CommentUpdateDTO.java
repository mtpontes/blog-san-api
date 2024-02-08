package br.com.blogsanapi.model.comment;

import jakarta.validation.constraints.NotNull;

public record CommentUpdateDTO(
		@NotNull
		Long id,
		String text) {
}
