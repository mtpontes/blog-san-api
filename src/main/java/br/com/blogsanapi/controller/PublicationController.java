package br.com.blogsanapi.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.blogsanapi.model.post.Publication;
import br.com.blogsanapi.model.post.PublicationRequestDTO;
import br.com.blogsanapi.model.post.PublicationResponseDTO;
import br.com.blogsanapi.model.post.PublicationUpdateRequestDTO;
import br.com.blogsanapi.service.PublicationService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/blog/publications")
public class PublicationController {
	private static Logger logger = LoggerFactory.getLogger(PublicationController.class);

	@Autowired
	private PublicationService publicationService;
	
	@PostMapping("/create")
	protected ResponseEntity<PublicationResponseDTO> createPublication(@RequestBody @Valid PublicationRequestDTO p, UriComponentsBuilder uriBuilder){
		Publication publi = publicationService.createPublication(p);
		
		var uri = uriBuilder.path("/blog/publications/{id}").buildAndExpand(p).toUri();
		
		return ResponseEntity.created(uri).body(new PublicationResponseDTO(publi));
	}
	@GetMapping("/{id}")
	protected ResponseEntity<PublicationResponseDTO> showPublication(@PathVariable Long id) {
		return ResponseEntity.ok(publicationService.getPublication(id));
	}
	@PutMapping("/update")
	protected ResponseEntity<PublicationResponseDTO> updatePublication(@RequestBody PublicationUpdateRequestDTO id) {
		return ResponseEntity.ok(publicationService.updatePublication(id));
	}
	@DeleteMapping
	protected ResponseEntity deletePublication(@PathVariable Long id) {
		return ResponseEntity.noContent().build();
	}
	
	
	
}
