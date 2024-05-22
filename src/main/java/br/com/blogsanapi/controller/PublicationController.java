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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.blogsanapi.model.comment.request.CommentRepliRequestDTO;
import br.com.blogsanapi.model.comment.request.CommentRequestDTO;
import br.com.blogsanapi.model.comment.request.CommentUpdateDTO;
import br.com.blogsanapi.model.comment.response.CommentResponseDTO;
import br.com.blogsanapi.model.publication.request.PublicationRequestDTO;
import br.com.blogsanapi.model.publication.request.PublicationUpdateRequestDTO;
import br.com.blogsanapi.model.publication.response.PublicationResponseDTO;
import br.com.blogsanapi.model.publication.response.PublicationResponseWithCommentsDTO;
import br.com.blogsanapi.service.CommentService;
import br.com.blogsanapi.service.PublicationService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/publications")
@SecurityRequirement(name = "bearer-key")
public class PublicationController {
	
	@Autowired
	private PublicationService publicationService;
	@Autowired
	private CommentService commentService;
	
	
	@PostMapping
	@Transactional
	protected ResponseEntity<PublicationResponseDTO> createPublication(@RequestBody @Valid PublicationRequestDTO dto, UriComponentsBuilder uriBuilder){
		PublicationResponseDTO dtoResponse = publicationService.createPublication(dto);
		
		var uri = uriBuilder.path("/blog/publications/{id}").buildAndExpand(dto).toUri();
		
		return ResponseEntity.created(uri).body(dtoResponse);
	}
	
	@GetMapping("/{id}")
	protected ResponseEntity<PublicationResponseWithCommentsDTO> getPublication(@PageableDefault(size = 5) Pageable pageable, @PathVariable Long id) {
		return ResponseEntity.ok(publicationService.getAPublicationWhithComments(pageable, id));
	}
	@GetMapping
	protected ResponseEntity<Page<PublicationResponseDTO>> getPublicationsByParams(
			@PageableDefault(size = 10) Pageable pageable,
			@RequestParam(name = "date", required = false) LocalDate date,
			@RequestParam(name = "userId", required = false) Long userId
			) {
		
		return ResponseEntity.ok(publicationService.getAllPublications(pageable, date, userId));
	}
	
	@PutMapping("/{publicationId}")
	@Transactional
	protected ResponseEntity<PublicationResponseDTO> updatePublication(
			@PathVariable Long publicationId, 
			@RequestBody @Valid PublicationUpdateRequestDTO dto
			) {
		
		return ResponseEntity.ok(publicationService.updatePublication(publicationId, dto));
	}
	
	@DeleteMapping("/{id}")
	@Transactional
	protected ResponseEntity<?> deletePublication(@PathVariable Long id) {
		publicationService.deletePublication(id);
		return ResponseEntity.noContent().build();
	}
	
	
	@PostMapping("/comments/create")
	public ResponseEntity<CommentResponseDTO> createComment(@RequestBody @Valid CommentRequestDTO dto, UriComponentsBuilder uriBuilder) {
		CommentResponseDTO commentResponse = commentService.createComment(dto);
		
		var uri = uriBuilder.path("/blog/comments/{id}").buildAndExpand(dto).toUri();
		
		return ResponseEntity.created(uri).body(commentResponse);
	}
	@PostMapping("/comments/create/reply")
	public ResponseEntity<CommentResponseDTO> replyComment(@RequestBody @Valid CommentRepliRequestDTO dto, UriComponentsBuilder uriBuilder) {
		CommentResponseDTO commentResponse = commentService.replyComment(dto);
		
		var uri = uriBuilder.path("/blog/comments/{id}").buildAndExpand(dto).toUri();
		
		return ResponseEntity.created(uri).body(commentResponse);
	}
	
	@GetMapping("/comments/replies/{id}")
	public ResponseEntity<Page<CommentResponseDTO>> getAllRepliesByComment(@PageableDefault(size = 5) Pageable pageable, @PathVariable Long id) {
		return ResponseEntity.ok(commentService.getRepliesByComment(pageable, id));
	}
	
	@GetMapping("/comments/by-user/{id}")
	public ResponseEntity<Page<CommentResponseDTO>> getAllCommentsByUser(@PageableDefault(size = 10) Pageable pageable, @PathVariable Long id) {
		return ResponseEntity.ok(commentService.getCommentsByUser(pageable, id));
	}
	@GetMapping("/comments/by-publication/{id}")
	public ResponseEntity<Page<CommentResponseDTO>> getCommentsByPublication(@PageableDefault(size = 10) Pageable pageable, @PathVariable Long id) {
		return ResponseEntity.ok(commentService.getCommentsByPublicationId(pageable, id));
	}
	
	@PutMapping("/comments/update/{id}")
	public ResponseEntity<CommentResponseDTO> updateComment(@RequestBody @Valid CommentUpdateDTO dto, @PathVariable Long id) {
		CommentResponseDTO commentResponse = commentService.updateComment(dto, id);
		return ResponseEntity.ok(commentResponse);
	}
	
	@DeleteMapping("/comments/delete/{id}")
	public ResponseEntity<?> deleteComment(@PathVariable Long id) {
		commentService.deleteComment(id);
		return ResponseEntity.noContent().build();
	}
}