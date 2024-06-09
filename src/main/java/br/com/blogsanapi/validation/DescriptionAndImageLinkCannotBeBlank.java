package br.com.blogsanapi.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target(ElementType.TYPE_USE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DescriptionAndImageLinkCannotBeBlankImpl.class)
public @interface DescriptionAndImageLinkCannotBeBlank {

    String message() default "Both fields cannot be blank";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}