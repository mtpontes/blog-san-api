package br.com.blogsanapi.model.user.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterDTO(
		@NotBlank
		String login,
		@NotBlank
		String password, 
		@NotBlank
		String name,
		@Email @NotBlank
		String email) {
}
