package br.com.blogsanapi.model.post;

import java.util.List;

import br.com.blogsanapi.model.comment.Comment;
import br.com.blogsanapi.model.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity(name = "Publication")
@Table(name = "publications")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String description;
    private String imageLink;
    
    @ManyToOne @JoinColumn(name = "user_id")
    private User user;
    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL)
    private List<Comment> comments;
    
    
    public Publication(String description, String imageLink, User user) {
    	this.description = description;
    	this.imageLink = imageLink;
    	this.user = user;
    }


	public void updateDescription(String description) {
		this.description = description;
	}
}	