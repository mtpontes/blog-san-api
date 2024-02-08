package br.com.blogsanapi.domain.user;

public record RegisterDTO(String login, String password, UserRole role) {
}
