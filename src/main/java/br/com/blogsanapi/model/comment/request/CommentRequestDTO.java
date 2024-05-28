package br.com.blogsanapi.model.comment.request;

import jakarta.validation.constraints.NotBlank;

public record CommentRequestDTO(@NotBlank String text) {}