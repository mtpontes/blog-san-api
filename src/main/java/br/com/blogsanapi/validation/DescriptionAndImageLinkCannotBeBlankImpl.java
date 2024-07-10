package br.com.blogsanapi.validation;

import br.com.blogsanapi.model.publication.request.PublicationRequestDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class DescriptionAndImageLinkCannotBeBlankImpl implements ConstraintValidator<DescriptionAndImageLinkCannotBeBlank, PublicationRequestDTO> {
	
	@Override
	public void initialize(DescriptionAndImageLinkCannotBeBlank constraintAnnotation) {
	}

	@Override
	public boolean isValid(PublicationRequestDTO data, ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();

		boolean descriptionIsValid = true;
		if (data.description() == null || data.description().isEmpty()) {
			context.buildConstraintViolationWithTemplate("Both fields cannot be blank")
				.addPropertyNode("description")
				.addConstraintViolation();
			descriptionIsValid = false;
		}

		boolean imageLinkIsValid = true;
		if (data.imageLink() == null || data.imageLink().isEmpty()) {
			context.buildConstraintViolationWithTemplate("Both fields cannot be blank")
				.addPropertyNode("imageLink")
				.addConstraintViolation();
			imageLinkIsValid = false;
		}

		return descriptionIsValid || imageLinkIsValid ? true : false;
	}	
}