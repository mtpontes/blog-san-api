package br.com.blogsanapi.model.comment.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.blogsanapi.model.comment.Comment;

public record CommentResponseDTO(
		Long commentId,
		Long userId,
		String text,
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
		LocalDateTime date,
		Boolean edited) {
	
	public CommentResponseDTO(Comment c) {
		this(c.getId(), c.getUser().getId(), c.getText(), c.getDate(), c.getEdited());
	}
}
