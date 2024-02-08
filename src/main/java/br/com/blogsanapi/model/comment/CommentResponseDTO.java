package br.com.blogsanapi.model.comment;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record CommentResponseDTO(
		Long commentId,
		Long userId,
		String text,
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
		LocalDateTime date) {
	
	public CommentResponseDTO(Comment c) {
		this(c.getId(), c.getUser().getId(), c.getText(), c.getDate());
	}
}
