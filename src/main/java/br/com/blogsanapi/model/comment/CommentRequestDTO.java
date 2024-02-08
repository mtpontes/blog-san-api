package br.com.blogsanapi.model.comment;

public record CommentRequestDTO(
		String text,
		Long publicationId
		) {

}
