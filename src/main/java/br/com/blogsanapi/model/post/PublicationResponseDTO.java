package br.com.blogsanapi.model.post;

public record PublicationResponseDTO(
		Long id,
		String description,
		String imageLink,
		String nameUser,
		Long userId
		) {
	
	public PublicationResponseDTO(Publication p) {
		this(p.getId(), p.getDescription(), p.getImageLink(), p.getUser().getName(), p.getUser().getId());
	}
}
