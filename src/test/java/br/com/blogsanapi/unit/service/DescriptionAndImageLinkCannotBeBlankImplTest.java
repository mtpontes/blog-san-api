package br.com.blogsanapi.unit.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.blogsanapi.model.publication.request.PublicationRequestDTO;
import br.com.blogsanapi.validation.DescriptionAndImageLinkCannotBeBlankImpl;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder;
import jakarta.validation.ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext;

@ExtendWith(MockitoExtension.class)
class DescriptionAndImageLinkCannotBeBlankImplTest {
	
	@Mock
	private ConstraintValidatorContext constraintValidatorContext;
	@Mock
	private ConstraintViolationBuilder  constraintViolationBuilder;
	@Mock
	private NodeBuilderCustomizableContext nodeBuilderCustomizableContext;

	@InjectMocks
	private DescriptionAndImageLinkCannotBeBlankImpl implementation;


    private void mockConstraintValidatorContext(int times) {
        for (int i = 0; i <= times; i++) {
            when(constraintValidatorContext.buildConstraintViolationWithTemplate(anyString()))
                    .thenReturn(constraintViolationBuilder);
            when(constraintViolationBuilder.addPropertyNode(anyString()))
                    .thenReturn(nodeBuilderCustomizableContext);
            when(nodeBuilderCustomizableContext.addConstraintViolation())
                    .thenReturn(constraintValidatorContext);
        }
    }

	@Test
    @DisplayName("Valid publication return true")
    void testIsValid_withValidInput() {
		var withBothFields = new PublicationRequestDTO("Description", "ImageLink");
		assertTrue(implementation.isValid(withBothFields, constraintValidatorContext), "Both populated fields are valid");

        var withValidDescriptionAndImageLinkBlank = new PublicationRequestDTO("Description", "");
        this.mockConstraintValidatorContext(1);
		assertTrue(implementation.isValid(withValidDescriptionAndImageLinkBlank, constraintValidatorContext), "It is only valid for ImageLink to be blank");
		
        var withValidDescriptionAndImageLinkNull = new PublicationRequestDTO("Description", null);
        this.mockConstraintValidatorContext(1);
		assertTrue(implementation.isValid(withValidDescriptionAndImageLinkNull, constraintValidatorContext), "It is only valid for ImageLink to be null");


        var withValidImageLinkAndDescriptionBlank = new PublicationRequestDTO("", "Image link");
        this.mockConstraintValidatorContext(1);
		assertTrue(implementation.isValid(withValidImageLinkAndDescriptionBlank, constraintValidatorContext), "It is only valid for Description to be blank");
		
        var withValidImageLinkAndDescriptionNull = new PublicationRequestDTO(null, "Image link");
        this.mockConstraintValidatorContext(1);
		assertTrue(implementation.isValid(withValidImageLinkAndDescriptionNull, constraintValidatorContext), "It is only valid for Description to be null");
    }

	@Test
    @DisplayName("Invalid publication return false")
    void testIsValid_withInvalidInput() {
		var withBothFieldsBlank = new PublicationRequestDTO("", "");
        this.mockConstraintValidatorContext(2);
		assertFalse(implementation.isValid(withBothFieldsBlank, constraintValidatorContext), "Both cannot be blank");

		var withBothFieldsNull = new PublicationRequestDTO(null, null);
        this.mockConstraintValidatorContext(2);
		assertFalse(implementation.isValid(withBothFieldsNull, constraintValidatorContext), "Both cannot be null");

        var withDescriptionBlankAndImageLinkNull = new PublicationRequestDTO("", null);
        this.mockConstraintValidatorContext(1);
		assertFalse(implementation.isValid(withDescriptionBlankAndImageLinkNull, constraintValidatorContext), "Blank with null must be invalid");
        
        var withDescriptionNullAndImageLinkBlank = new PublicationRequestDTO(null, "");
        this.mockConstraintValidatorContext(1);
		assertFalse(implementation.isValid(withDescriptionNullAndImageLinkBlank, constraintValidatorContext), "Null with blank must be invalid");
    }
}