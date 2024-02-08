package br.com.blogsanapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.blogsanapi.model.comment.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>{

}
