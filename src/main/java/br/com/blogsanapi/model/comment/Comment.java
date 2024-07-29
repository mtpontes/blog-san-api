package br.com.blogsanapi.model.comment;

import java.time.LocalDateTime;
import java.util.List;

import br.com.blogsanapi.model.publication.Publication;
import br.com.blogsanapi.model.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity(name = "Comment")
@Table(name = "comments")
@Builder
@AllArgsConstructor
@Getter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Comment {
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String text;
    private LocalDateTime date;
    private Boolean edited;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "publication_id")
    private Publication publication;

    @ManyToOne 
    @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;

    @OneToMany(
        mappedBy = "parentComment", 
        cascade = CascadeType.ALL, 
        fetch = FetchType.LAZY)
    private List<Comment> replies;


    public Comment(
        String text, 
        User user, 
        Publication publication, 
        Comment parentComment
    ) {
        this.validateText(text);
        
        this.text = text;
        this.user = user;
        this.date = LocalDateTime.now();
        this.edited = false;

        this.publication = publication;
        this.parentComment = parentComment;
    }

    public void updateText(String text) {
        this.validateText(text);
        
        this.text = text;
        this.date = LocalDateTime.now();
        this.edited = true;
    }

    private void validateText(String text) {
        if (text == null || text.isBlank()) 
            throw new IllegalArgumentException("It is not possible to comment with empty text");
    }

    public Comment getCommentReference() {
        return this.parentComment == null ? this : this.parentComment;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", text='" + text + '\'' +
                ", date=" + date +
                ", edited=" + edited +
                ", user=" + user +
                '}';
    }
}