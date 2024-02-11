package br.com.blogsanapi.model.comment.request;

import jakarta.validation.constraints.NotNull;

public record CommentRepliRequestDTO(
		@NotNull
		Long targetCommentId,
		String text
		) {
}
