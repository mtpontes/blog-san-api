package br.com.blogsanapi.integration.repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;

import br.com.blogsanapi.integration.MySQLTestContainer;
import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.publication.Publication;
import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.repository.CommentRepository;
import br.com.blogsanapi.repository.PublicationRepository;
import br.com.blogsanapi.repository.UserRepository;

@SpringBootTest
@ExtendWith(MySQLTestContainer.class)
@DirtiesContext
public class CommentRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PublicationRepository publicationRepository;

    @Test
    @DisplayName("Test finding all comments by publication ID")
    void findAllByParamsTest() {
    	// arrange
        User user = userRepository.save(User.builder().name("User-san").login("login").build());
        List<Publication> publications = List.of(
                new Publication("INTROMETIDO 1", "link1", user),
                new Publication("INTROMETIDO 2", "link2", user)
        );
        publicationRepository.saveAll(publications);

        List<Comment> comments = List.of(
                new Comment("c1", user, publications.get(0), null),
                new Comment("c2", user, publications.get(0), null),
                new Comment("c3", user, publications.get(0), null),
                new Comment("c4", user, publications.get(0), null),
                new Comment("c5", user, publications.get(0), null)
        );
        commentRepository.saveAll(comments);
        Map<Long, Comment> commentsMap = comments.stream().collect(Collectors.toMap(Comment::getId, c -> c));

        // act
        List<Comment> recovers = commentRepository.findAllByPublicationId(
                PageRequest.of(0, 3), publications.get(0).getId())
                        .getContent();

        // assert
        Assertions.assertEquals(3, recovers.size(), "Should have returned 3 comments");
        recovers.forEach(c -> {
                Assertions.assertEquals(commentsMap.get(c.getId()).getId(), c.getId(),"Comment IDs should match");
                Assertions.assertEquals(commentsMap.get(c.getId()).getText(), c.getText(),"Comment text should match");
        });
    }

    @Test
    @DisplayName("Test finding all replies")
    void findAllRepliesTest() {
    	// arrange
        User user = userRepository.save(User.builder().name("User-san").login("login").build());
        Publication publication = publicationRepository.save(new Publication("p1", "link1", user));

        List<Comment> comments = List.of(
                new Comment("c2", user, publication, null),
                new Comment("c3", user, publication, null)
        );
        commentRepository.saveAll(comments);

        List<Comment> replies = List.of(
                new Comment("c2", user, null, comments.get(0)),
                new Comment("c3", user, null, comments.get(0)),
                new Comment("c4", user, null, comments.get(0))
        );
        commentRepository.saveAll(replies);
        Map<Long, Comment> commentsMap = replies.stream().collect(Collectors.toMap(Comment::getId, c -> c));

        // act
        List<Comment> recovers = commentRepository.findAllReplies(PageRequest.of(0, 2), publication.getId())
                .getContent();

        // assert
        Assertions.assertEquals(2, recovers.size(), "Should have returned 2 replies");
        recovers.forEach(c -> {
            Assertions.assertEquals(commentsMap.get(c.getId()).getId(), c.getId(), "Comment IDs should match");
            Assertions.assertEquals(comments.get(0).getId(), c.getParentComment().getId(), "Parent comment should be the same");
        });
    }
}