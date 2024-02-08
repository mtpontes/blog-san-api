package br.com.blogsanapi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.blogsanapi.model.comment.CommentRequestDTO;
import br.com.blogsanapi.model.comment.CommentUpdateDTO;
import br.com.blogsanapi.service.CommentService;

@RestController
@RequestMapping("/comment")
public class CommentController {

	@Autowired
	private CommentService service;
	

	@PostMapping("/create")
	public void createComment(@RequestBody CommentRequestDTO dto) {
		
	}
	@GetMapping("/{id}")
	public void showComment(@PathVariable Long id) {
		
	}
	@PutMapping("/update")
	public void updateComment(@RequestBody CommentUpdateDTO dto) {
		
	}
	@DeleteMapping("/delete/{id}")
	public void deleteComment(@PathVariable Long id) {
		
	}
}
