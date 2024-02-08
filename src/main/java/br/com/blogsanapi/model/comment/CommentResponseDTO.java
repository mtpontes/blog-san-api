package br.com.blogsanapi.model.comment;

public record CommentResponseDTO(
		Long commentId,
		Long userId,
		String text) {
	
	public CommentResponseDTO(Comment c) {
		this(c.getId(), c.getUser().getId(), c.getText());
	}
}
