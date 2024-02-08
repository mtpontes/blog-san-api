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

import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.comment.CommentRequestDTO;
import br.com.blogsanapi.model.comment.CommentResponseDTO;
import br.com.blogsanapi.model.comment.CommentUpdateDTO;
import br.com.blogsanapi.service.CommentService;

@RestController
@RequestMapping("/blog/comments")
public class CommentController {

	@Autowired
	private CommentService service;
	

	@PostMapping("/create")
	public ResponseEntity<CommentResponseDTO> createComment(@RequestBody CommentRequestDTO dto, UriComponentsBuilder uriBuilder) {
		Comment comment = service.createComment(dto);
		
		var uri = uriBuilder.path("/blog/comments/{id}").buildAndExpand(dto).toUri();
		
		return ResponseEntity.created(uri).body(new CommentResponseDTO(comment));
	}
	
	@GetMapping("/by-user/{id}")
	public ResponseEntity<Page<CommentResponseDTO>> getAllCommentsByUser(@PageableDefault(size = 10) Pageable pageable, @PathVariable Long id) {
		return ResponseEntity.ok(service.getCommentsByUser(pageable, id).map(CommentResponseDTO::new));
	}
	
	@PutMapping("/update")
	public ResponseEntity<CommentResponseDTO> updateComment(@RequestBody CommentUpdateDTO dto) {
		Comment comment = service.updateComment(dto);
		return ResponseEntity.ok(new CommentResponseDTO(comment));
	}
	
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<?> deleteComment(@PathVariable Long id) {
		service.deleteComment(id);
		return ResponseEntity.noContent().build();
	}
	
}
