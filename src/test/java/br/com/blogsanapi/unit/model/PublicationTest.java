package br.com.blogsanapi.unit.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.blogsanapi.model.publication.Publication;

class PublicationTest {
	
	@Test
	@DisplayName("Must throw exception when instantiating Publication with `description` and `imageLink` null")
	void instanciatePublicatonTest01() {
		Assertions.assertThrows(
				IllegalArgumentException.class, 
				() -> new Publication(null, null, null), 
				"Instantiating with both null attributes");
	}
	
	@Test
	@DisplayName("Must throw exception when instantiating Publication with `description` and `imageLink` blank")
	void instanciatePublicatonTest02() {
		Assertions.assertThrows(
				IllegalArgumentException.class, 
				() -> new Publication("", "", null), 
				"Instanciating with both blank attributes");
	}
	
	@Test
	@DisplayName("Must instantiate Publication with at least one valid value for `description` and `imageLink`")
	void instanciatePublicatonTest03() {
		Assertions.assertDoesNotThrow(() -> new Publication("description", "", null), "Instanciating with `imageLink` blank");
		Assertions.assertDoesNotThrow(() -> new Publication("description", null, null), "Instanciating with `imageLink` null");
		
		Assertions.assertDoesNotThrow(() -> new Publication("", "imageLink", null), "Instanciating with `description` blank");
		Assertions.assertDoesNotThrow(() -> new Publication(null, "imageLink", null), "Instanciating with `description` null");
	}
	
	@Test
	@DisplayName("Instance with `imageLink` null must throw exception when updating `description` with invalid value")
	void updateDescriptionTest01() {
		Publication publiImageLinkNull = Publication.builder().description("description").imageLink(null).build();

		// passing null
		Assertions.assertThrows(
				IllegalArgumentException.class, 
				() -> publiImageLinkNull.updateDescription(null) ,
				"Updating with null value");
		// passing blank
		Assertions.assertThrows(
				IllegalArgumentException.class, 
				() -> publiImageLinkNull.updateDescription("") ,
				"Updating with blank value");
	}
	
	@Test
	@DisplayName("Instance with `imageLink` blank must throw exception when updating `description` with invalid value")
	void updateDescriptionTest02() {
		Publication publiImageLinkBlank = Publication.builder().description("description").imageLink("").build();
		
		// passing null
		Assertions.assertThrows(
				IllegalArgumentException.class, 
				() -> publiImageLinkBlank.updateDescription(null) ,
				"Updating with null value");
		// passing blank
		Assertions.assertThrows(
				IllegalArgumentException.class, 
				() -> publiImageLinkBlank.updateDescription("") ,
				"Updating with blank value");
	}
	
	@Test
	@DisplayName("Must update `description` without throwing exception")
	void updateDescriptionTest03() {
		// arrange
		Publication publiDescriptionNull = Publication.builder().description(null).imageLink("imageLink").build();
		
		// act and assert
        Assertions.assertDoesNotThrow(() -> publiDescriptionNull.updateDescription(null), "Updating with null description should not throw exception");
        Assertions.assertNull(publiDescriptionNull.getDescription(), "Description should be null");

        Assertions.assertDoesNotThrow(() -> publiDescriptionNull.updateDescription(""), "Updating with empty description should not throw exception");
        Assertions.assertTrue(publiDescriptionNull.getDescription().isBlank(), "Description should be blank");

        Assertions.assertDoesNotThrow(() -> publiDescriptionNull.updateDescription("update"), "Updating with 'update' description should not throw exception");
        Assertions.assertEquals("update", publiDescriptionNull.getDescription(), "Description should be 'update'");
	}
	
	@Test
	@DisplayName("Must update `description` without throwing exception")
	void updateDescriptionTest04() {
		// arrange
		Publication publiDescriptionBlank = Publication.builder().description("").imageLink("ImageLink").build();
		
		// act and assert
		Assertions.assertDoesNotThrow(() -> publiDescriptionBlank.updateDescription(null), "Updating with null description should not throw exception");
		Assertions.assertNull(publiDescriptionBlank.getDescription(), "Description should be null");
		
		Assertions.assertDoesNotThrow(() -> publiDescriptionBlank.updateDescription(""), "Updating with empty description should not throw exception");
		Assertions.assertTrue(publiDescriptionBlank.getDescription().isBlank(), "Description should be blank");
		
		Assertions.assertDoesNotThrow(() -> publiDescriptionBlank.updateDescription("update"), "Updating with 'update' description should not throw exception");
		Assertions.assertEquals("update", publiDescriptionBlank.getDescription(), "Description should be 'update'");
	}
}