package br.com.blogsanapi.model.user.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthenticationDTO(
		@NotBlank
		String login, 
		@NotBlank
		String password) {
}
