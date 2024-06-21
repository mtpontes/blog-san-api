package br.com.blogsanapi.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.blogsanapi.model.publication.Publication;

public interface PublicationRepository extends JpaRepository<Publication, Long>{

	@Query("""
		SELECT p FROM Publication p 
		WHERE
		(:date IS NULL OR DATE(p.date) = :date)
		AND
		(:userId IS NULL OR p.user.id = :userId)
		""")
	Page<Publication> findAllByParams(Pageable pageable, LocalDate date, Long userId);
	
	void deleteByUserIdAndId(Long userId, Long publiId);

	Page<Publication> findAllByUserId(Pageable pageable, Long id);

	Optional<Publication> findByIdAndUserId(Long publicationId, Long id);
}