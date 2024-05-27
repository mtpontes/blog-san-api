package br.com.blogsanapi.model.publication.request;

import br.com.blogsanapi.validation.DescriptionAndImageLinkCannotBeBlank;

@DescriptionAndImageLinkCannotBeBlank
public record PublicationRequestDTO(String description, String imageLink) {}