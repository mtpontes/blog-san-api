package br.com.blogsanapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.blogsanapi.model.post.Publication;

public interface PublicationRepository extends JpaRepository<Publication, Long>{

}
