package br.com.blogsanapi.model.comment.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import br.com.blogsanapi.model.comment.Comment;

public record CommentResponseDTO(
		Long commentId,
		Long userId,
		String nameUser,
		String text,
		@JsonFormat(pattern = "yyyy-MM-dd HH:mm")
		LocalDateTime date,
		Boolean edited,
		Long parentCommentId) {
	
	public CommentResponseDTO(Comment c) {
		this(
			c.getId(), 
			c.getUser().getId(), 
			c.getUser().getName() , 
			c.getText(), c.getDate(), 
			c.getEdited(), 
			c.getParentComment() == null ? null : c.getParentComment().getId()
		);
	}
}