package br.com.blogsanapi.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import br.com.blogsanapi.model.publication.request.PublicationRequestDTO;
import br.com.blogsanapi.model.publication.request.PublicationUpdateRequestDTO;
import br.com.blogsanapi.model.publication.response.PublicationResponseDTO;
import br.com.blogsanapi.model.publication.response.PublicationResponseWithCommentsDTO;
import br.com.blogsanapi.service.PublicationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/blog/publications")
@SecurityRequirement(name = "bearer-key")
public class PublicationController {
	@Autowired
	private PublicationService publicationService;
	
	@PostMapping("/create")
	@Transactional
	protected ResponseEntity<PublicationResponseDTO> createPublication(@RequestBody @Valid PublicationRequestDTO dto, UriComponentsBuilder uriBuilder){
		PublicationResponseDTO dtoResponse = publicationService.createPublication(dto);
		
		var uri = uriBuilder.path("/blog/publications/{id}").buildAndExpand(dto).toUri();
		
		return ResponseEntity.created(uri).body(dtoResponse);
	}
	
	@GetMapping("/{id}")
	protected ResponseEntity<PublicationResponseWithCommentsDTO> test(@PageableDefault(size = 5) Pageable pageable, @PathVariable Long id) {
		return ResponseEntity.ok(publicationService.getAPublicationWhithComments(pageable, id));
	}
	@GetMapping("/all")
	protected ResponseEntity<Page<PublicationResponseDTO>> getPublicationsByDate(@PageableDefault(size = 10) Pageable pageable) {
		return ResponseEntity.ok(publicationService.getAllPublications(pageable));
	}
	@GetMapping("/by-date/{date}")
	protected ResponseEntity<Page<PublicationResponseDTO>> getPublicationsByDate(@PageableDefault(size = 10) Pageable pageable, @PathVariable @Valid LocalDate date) {
		return ResponseEntity.ok(publicationService.getAllPublicationsByDate(pageable, date));
	}
	@GetMapping("/by-user/{id}")
	protected ResponseEntity<Page<PublicationResponseDTO>> getAllPublicationsByUser(@PageableDefault(size = 10) Pageable pageable, @PathVariable Long id) {
		return ResponseEntity.ok(publicationService.getAllPublicationsByUser(pageable, id));
	}
	
	@PutMapping("/update")
	@Transactional
	protected ResponseEntity<PublicationResponseDTO> updatePublication(@RequestBody @Valid PublicationUpdateRequestDTO dto) {
		return ResponseEntity.ok(publicationService.updatePublication(dto));
	}
	
	@DeleteMapping("/delete/{id}")
	@Transactional
	protected ResponseEntity<?> deletePublication(@PathVariable Long id) {
		publicationService.deletePublication(id);
		return ResponseEntity.noContent().build();
	}
}
