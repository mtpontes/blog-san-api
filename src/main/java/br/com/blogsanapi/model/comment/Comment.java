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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Comment")
@Table(name = "comments")
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
    
    @ManyToOne @JoinColumn(name = "parent_comment_id")
    private Comment parentComment;
    
    @OneToMany(mappedBy = "parentComment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> replies;
    
    
    public Comment(String text, User user, Publication publication) {
    	this.text = text;
    	this.user = user;
    	this.publication = publication;
    	this.date = LocalDateTime.now();
    	this.edited = false;
    }
    
    public Comment(String text, User user, Comment comment) {
    	this.text = text;
    	this.user = user;
    	this.publication = comment.getPublication();
    	this.parentComment = comment;
    	this.date = LocalDateTime.now();
    	this.edited = false;
    }

	public void updateText(String text) {
		this.text = text;
		this.date = LocalDateTime.now();
		this.edited = true;
	}
}
