package br.com.blogsanapi.model.comment.request;

import jakarta.validation.constraints.NotNull;

public record CommentRequestDTO(
		@NotNull
		Long publicationId,
		String text
		) {
}
