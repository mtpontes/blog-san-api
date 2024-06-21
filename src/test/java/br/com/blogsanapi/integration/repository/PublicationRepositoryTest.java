package br.com.blogsanapi.integration.repository;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;

import br.com.blogsanapi.configs.RepositoryTest;
import br.com.blogsanapi.model.publication.Publication;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.repository.PublicationRepository;
import br.com.blogsanapi.repository.UserRepository;

@RepositoryTest
public class PublicationRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Test
    @DisplayName("Test finding all publications by date and user ID")
    void findAllByParamsTest() {
    	// arrange
        User user1 = userRepository.save(User.builder().name("User-san").login("login").build());
        User user2 = userRepository.save(User.builder().name("User-san").login("login").build());
        List<Publication> publications = List.of(
            new Publication("p1", "link1", user1),
            new Publication("p2", "link2", user1),
            new Publication("p3", "link3", user1),
            new Publication("p4", "link4", user1),
            new Publication("p5", "link5", user2),
            new Publication("p6", "link6", user2),
            new Publication("p7", "link7", user2)
        );
        publicationRepository.saveAll(publications);

        // act
        var PAGEABLE_DEFAULT = PageRequest.of(0, 20);
        var DATE_SEARCHED = LocalDate.now();
        var USER_ID_SEARCHED = user1.getId();

        List<Publication> queryByDefault = publicationRepository
            .findAllByParams(PageRequest.of(0, 4), null, null).getContent();

        List<Publication> queryByDate = publicationRepository
            .findAllByParams(PAGEABLE_DEFAULT, DATE_SEARCHED, null).getContent();

        List<Publication> queryByUserId = publicationRepository
            .findAllByParams(PAGEABLE_DEFAULT, null, USER_ID_SEARCHED).getContent();

        List<Publication> queryByAllParams = publicationRepository
            .findAllByParams(PAGEABLE_DEFAULT, DATE_SEARCHED, USER_ID_SEARCHED).getContent();

        // assert
        Assertions.assertEquals(4, queryByDefault.size(), "Should have returned 3 comments");
        Assertions.assertEquals(publications.size(), queryByDate.size(), "Should have returned 3 comments");
        Assertions.assertEquals(4, queryByUserId.size(), "Should have returned 3 comments");
        Assertions.assertEquals(4, queryByAllParams.size(), "Should have returned 3 comments");
    }
}