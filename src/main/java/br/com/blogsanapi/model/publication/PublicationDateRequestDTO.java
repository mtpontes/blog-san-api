package br.com.blogsanapi.model.publication;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record PublicationDateRequestDTO(
		@NotNull
		LocalDate date) {
}
