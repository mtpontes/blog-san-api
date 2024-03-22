package br.com.blogsanapi.controller;

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

import br.com.blogsanapi.model.comment.request.CommentRepliRequestDTO;
import br.com.blogsanapi.model.comment.request.CommentRequestDTO;
import br.com.blogsanapi.model.comment.request.CommentUpdateDTO;
import br.com.blogsanapi.model.comment.response.CommentResponseDTO;
import br.com.blogsanapi.service.CommentService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/blog/comments")
@SecurityRequirement(name = "bearer-key")
public class CommentController {

	@Autowired
	private CommentService service;
	

	@PostMapping("/create")
	public ResponseEntity<CommentResponseDTO> createComment(@RequestBody @Valid CommentRequestDTO dto, UriComponentsBuilder uriBuilder) {
		CommentResponseDTO commentResponse = service.createComment(dto);
		
		var uri = uriBuilder.path("/blog/comments/{id}").buildAndExpand(dto).toUri();
		
		return ResponseEntity.created(uri).body(commentResponse);
	}
	@PostMapping("/create/reply")
	public ResponseEntity<CommentResponseDTO> replyComment(@RequestBody @Valid CommentRepliRequestDTO dto, UriComponentsBuilder uriBuilder) {
		CommentResponseDTO commentResponse = service.replyComment(dto);
		
		var uri = uriBuilder.path("/blog/comments/{id}").buildAndExpand(dto).toUri();
		
		return ResponseEntity.created(uri).body(commentResponse);
	}
	
	@GetMapping("/replies/{id}")
	public ResponseEntity<Page<CommentResponseDTO>> getAllRepliesByComment(@PageableDefault(size = 5) Pageable pageable, @PathVariable Long id) {
		return ResponseEntity.ok(service.getRepliesByComment(pageable, id));
	}
	@GetMapping("/by-user/{id}")
	public ResponseEntity<Page<CommentResponseDTO>> getAllCommentsByUser(@PageableDefault(size = 10) Pageable pageable, @PathVariable Long id) {
		return ResponseEntity.ok(service.getCommentsByUser(pageable, id));
	}
	@GetMapping("/by-publication/{id}")
	public ResponseEntity<Page<CommentResponseDTO>> getCommentsByPublication(@PageableDefault(size = 10) Pageable pageable, @PathVariable Long id) {
		return ResponseEntity.ok(service.getCommentsByPublicationId(pageable, id));
	}
	
	@PutMapping("/update/{id}")
	public ResponseEntity<CommentResponseDTO> updateComment(@RequestBody @Valid CommentUpdateDTO dto, @PathVariable Long id) {
		CommentResponseDTO commentResponse = service.updateComment(dto, id);
		return ResponseEntity.ok(commentResponse);
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteComment(@PathVariable Long id) {
		service.deleteComment(id);
		return ResponseEntity.noContent().build();
	}
	
}
