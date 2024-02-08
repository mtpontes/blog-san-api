package br.com.blogsanapi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import br.com.blogsanapi.model.publication.Publication;
import br.com.blogsanapi.model.publication.PublicationDateRequestDTO;
import br.com.blogsanapi.model.publication.PublicationRequestDTO;
import br.com.blogsanapi.model.publication.PublicationUpdateRequestDTO;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.repository.PublicationRepository;

@Service
public class PublicationService {
//	private static Logger logger = LoggerFactory.getLogger(PublicationService.class);

	@Autowired
	private PublicationRepository repository;
	
	public Publication createPublication(PublicationRequestDTO publication) {
		var user = this.getUser();
		
		Publication publi = new Publication(publication.description(), publication.imageLink(), user);
		repository.save(publi);
		
		return publi;
	}
	
	public Publication getPublication(Long id) {
		return repository.getReferenceById(id);
	}

	public Page<Publication> getAllPublications(Pageable pageable) {
		return repository.findAll(pageable);
	}
	public Page<Publication> getAllPublicationsByDate(Pageable pageable, PublicationDateRequestDTO dto) {
		return repository.findAllByDate(pageable, dto.date());
	}
	public Page<Publication> getAllPublicationsByUser(Pageable pageable, Long id) {
		return repository.findAllByUserId(pageable, id);
	}
	
	public Publication updatePublication(PublicationUpdateRequestDTO dto) {
		Publication publi = repository.getReferenceById(dto.id());
		this.accesVerify(publi);
		
		publi.updateDescription(dto.description());
		return publi;
	}
	public void deletePublication(Long id) {
		User user = this.getUser();
		repository.deleteByUserIdAndId(user.getId(), id);
	}
	
	private User getUser() {
		return (User) SecurityContextHolder
				.getContext()
				.getAuthentication()
				.getPrincipal();
	}
	private void accesVerify(Publication publi) throws AccessDeniedException {
		User userByToken = this.getUser();
		User userByPubli = publi.getUser();
		if (userByToken == null || !userByPubli.getId().equals(userByToken.getId())) throw new AccessDeniedException("User do not have permission for access this task");
	}
}
