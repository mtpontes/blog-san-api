package br.com.blogsanapi.unit.model.publication;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import br.com.blogsanapi.model.publication.Publication;

class PublicationTest {
	
	@Test
	@DisplayName("Deve lançar exception ao instanciar Publication com `description` e `imageLink` null")
	void instanciatePublicatonTest01() {
		Assertions.assertThrows(
				IllegalArgumentException.class, 
				() -> new Publication(null, null, null), 
				"Instanciando com os dois atributos null");
	}
	
	@Test
	@DisplayName("Deve lançar exception ao instanciar Publication com `description` e `imageLink` blank")
	void instanciatePublicatonTest02() {
		Assertions.assertThrows(
				IllegalArgumentException.class, 
				() -> new Publication("", "", null), 
				"Instanciando com os dois atributos blank");
	}
	
	@Test
	@DisplayName("Deve instanciar Publication com pelo menos um valor válido para `description` e `imageLink`")
	void instanciatePublicatonTest03() {
		Assertions.assertDoesNotThrow(() -> new Publication("description", "", null), "Instanciando com `imageLink` blank");
		Assertions.assertDoesNotThrow(() -> new Publication("description", null, null), "Instanciando com `imageLink` null");
		
		Assertions.assertDoesNotThrow(() -> new Publication("", "imageLink", null), "Instanciando com `description` blank");
		Assertions.assertDoesNotThrow(() -> new Publication(null, "imageLink", null), "Instanciando com `description` null");
	}
	
	@Test
	@DisplayName("Instancia com `imageLink` null deve lançar exception ao atualizar `description` com valor inválido")
	void updateDescriptionTest01() {
		Publication publiImageLinkNull = new Publication("description", null, null);
		
		// passando null
		Assertions.assertThrows(
				IllegalArgumentException.class, 
				() -> publiImageLinkNull.updateDescription(null) ,
				"Updating with null value");
		// passando blank
		Assertions.assertThrows(
				IllegalArgumentException.class, 
				() -> publiImageLinkNull.updateDescription("") ,
				"Updating with blank value");
	}
	
	@Test
	@DisplayName("Instancia com `imageLink` blank deve lançar exception ao atualizar `description` com valor inválido")
	void updateDescriptionTest02() {
		Publication publiImageLinkBlank = new Publication("description", "", null);
		
		// passando null
		Assertions.assertThrows(
				IllegalArgumentException.class, 
				() -> publiImageLinkBlank.updateDescription(null) ,
				"Updating with null value");
		// passando blank
		Assertions.assertThrows(
				IllegalArgumentException.class, 
				() -> publiImageLinkBlank.updateDescription("") ,
				"Updating with blank value");
	}
	
	@Test
	@DisplayName("Deve atualizar `description` sem lançar exception")
	void updateDescriptionTest03() {
		// arrange
		Publication publiDescriptionNull = new Publication(null, "imageLink", null);
		
		// act and assert
		
        // Testando atualizações em publiImageLinkNull
        Assertions.assertDoesNotThrow(() -> publiDescriptionNull.updateDescription(null), "Updating with null description should not throw exception");
        Assertions.assertNull(publiDescriptionNull.getDescription(), "Description should be null");

        Assertions.assertDoesNotThrow(() -> publiDescriptionNull.updateDescription(""), "Updating with empty description should not throw exception");
        Assertions.assertTrue(publiDescriptionNull.getDescription().isBlank(), "Description should be blank");

        Assertions.assertDoesNotThrow(() -> publiDescriptionNull.updateDescription("update"), "Updating with 'update' description should not throw exception");
        Assertions.assertEquals("update", publiDescriptionNull.getDescription(), "Description should be 'update'");
	}
	
	@Test
	@DisplayName("Deve atualizar `description` sem lançar exception")
	void updateDescriptionTest04() {
		// arrange
		Publication publiDescriptionBlank = new Publication("", "ImageLink", null);
		
		// act and assert
		
		Assertions.assertDoesNotThrow(() -> publiDescriptionBlank.updateDescription(null), "Updating with null description should not throw exception");
		Assertions.assertNull(publiDescriptionBlank.getDescription(), "Description should be null");
		
		Assertions.assertDoesNotThrow(() -> publiDescriptionBlank.updateDescription(""), "Updating with empty description should not throw exception");
		Assertions.assertTrue(publiDescriptionBlank.getDescription().isBlank(), "Description should be blank");
		
		Assertions.assertDoesNotThrow(() -> publiDescriptionBlank.updateDescription("update"), "Updating with 'update' description should not throw exception");
		Assertions.assertEquals("update", publiDescriptionBlank.getDescription(), "Description should be 'update'");
	}
}