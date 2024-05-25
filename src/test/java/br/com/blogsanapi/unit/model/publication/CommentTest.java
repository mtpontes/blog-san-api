package br.com.blogsanapi.unit.model.publication;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.publication.Publication;
import br.com.blogsanapi.model.user.User;

class CommentTest {

	@Test
	@DisplayName("Must throw exception when passing invalid value to `text`")
	void instanciateCommentTest01() {
		Assertions.assertThrows(
				IllegalArgumentException.class, 
				() -> new Comment(null, new User(), new Publication(), new Comment()),
				"Instanciating with `text` null");
		
		Assertions.assertThrows(
				IllegalArgumentException.class, 
				() -> new Comment("", new User(), new Publication(), new Comment()),
				"Instanciating with `text` blank");
	}
	
	@Test
	@DisplayName("Should not throw exception when passing valid value to `text`")
	void instanciateCommentTest02() {
		Assertions.assertDoesNotThrow(
				() -> new Comment("text", new User(), new Publication(), new Comment()),
				"Instanciating with valid `text` value");
	}

	@Test
	@DisplayName("Should throw exception when passing invalid value")
	void updateTextTest01() {
		Comment comment = new Comment("text", new User(), new Publication(), new Comment());
		
		Assertions.assertThrows(
				IllegalArgumentException.class, 
				() -> comment.updateText(null),
				"Updating with null");
		
		Assertions.assertThrows(
				IllegalArgumentException.class, 
				() -> comment.updateText(""), 
				"Updating blank");
	}
	
	@Test
	@DisplayName("Should not throw exception when passing valid value")
	void updateTextTest02() {
		String NEW_TEXT_VALUE = "updated";
		Comment comment = new Comment("text", new User(), new Publication(), new Comment());
		
		Assertions.assertDoesNotThrow(
				() -> comment.updateText(NEW_TEXT_VALUE),
				"Updating with valid value");
		
		Assertions.assertEquals(comment.getText(), NEW_TEXT_VALUE);
	}
}