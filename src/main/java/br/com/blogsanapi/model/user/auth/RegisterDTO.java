package br.com.blogsanapi.model.user.auth;

public record RegisterDTO(String login, String password, String name, String email) {
}
