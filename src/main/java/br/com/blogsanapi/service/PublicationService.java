package br.com.blogsanapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.blogsanapi.model.post.Publication;
import br.com.blogsanapi.model.post.PublicationRequestDTO;
import br.com.blogsanapi.model.post.PublicationResponseDTO;
import br.com.blogsanapi.model.post.PublicationUpdateRequestDTO;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.repository.PublicationRepository;
import jakarta.validation.Valid;

@Service
public class PublicationService {
//	private static Logger logger = LoggerFactory.getLogger(PublicationService.class);

	@Autowired
	private PublicationRepository rp;
	
	public Publication createPublication(@Valid PublicationRequestDTO p) {
		var user = this.getUser();
		
		Publication publi = new Publication(p.description(), p.imageLink(), user);
		rp.save(publi);
		
		return publi;
	}
	
	public PublicationResponseDTO getPublication(Long id) {
		return new PublicationResponseDTO(rp.getReferenceById(id));
	}
	
	public PublicationResponseDTO updatePublication(PublicationUpdateRequestDTO dto) {
		Publication pb = rp.getReferenceById(dto.id());
		pb.updateDescription(dto.description());
		return new PublicationResponseDTO(pb);
	}

	
	
	
	
	
	private User getUser() {
		return (User) SecurityContextHolder
				.getContext()
				.getAuthentication()
				.getPrincipal();
	}
}
