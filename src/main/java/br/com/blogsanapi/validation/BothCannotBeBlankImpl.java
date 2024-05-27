package br.com.blogsanapi.validation;

import br.com.blogsanapi.model.publication.request.PublicationRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class BothCannotBeBlankImpl implements ConstraintValidator<DescriptionAndImageLinkCannotBeBlank, PublicationRequestDTO> {

	private String defaultMessage;
	
	@Override
	public void initialize(DescriptionAndImageLinkCannotBeBlank constraintAnnotation) {
		this.defaultMessage = constraintAnnotation.message();
	}
	
	@Override
	public boolean isValid(PublicationRequestDTO data, ConstraintValidatorContext context) {
		try {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate(defaultMessage).addConstraintViolation();
			
			return !data.description().isBlank() && !data.imageLink().isBlank();
			
		} catch(NullPointerException ex) {
			return false;
		}
	}	
}