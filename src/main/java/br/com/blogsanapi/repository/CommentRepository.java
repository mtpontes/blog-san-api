package br.com.blogsanapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.publication.Publication;

public interface CommentRepository extends JpaRepository<Comment, Long>{

	Page<Comment> findAllById(Pageable pageable, Long id);

	Page<Comment> findAllByUserId(Pageable pageable, Long id);

	void deleteByUserIdAndId(Long userId, Long commentId);

	Page<Comment> findAllByPublicationId(Pageable pageable, Long publicationId);

	Page<Comment> findByPublicationId(Pageable pageable, Long id);

	@Query("SELECT c FROM Comment c WHERE c.parentComment.id = :id")
	Page<Comment> findAllReplies(Pageable pageable, Long id);
}
