package br.com.blogsanapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import br.com.blogsanapi.model.comment.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>{

	Page<Comment> findAllById(Pageable pageable, Long id);

	Page<Comment> findAllByUserId(Pageable pageable, Long id);

	void deleteByUserIdAndId(Long userId, Long commentId);

}
