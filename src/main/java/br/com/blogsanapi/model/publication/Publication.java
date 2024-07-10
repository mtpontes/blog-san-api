package br.com.blogsanapi.model.publication;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import br.com.blogsanapi.model.comment.Comment;
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
import lombok.Setter;

@Entity(name = "Publication")
@Table(name = "publications")
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Publication {
	
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) @Setter
    private Long id;

    private String description;
    private String imageLink;
    private LocalDateTime date;
    private Boolean edited;

    @ManyToOne @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "publication", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();


    public Publication(String description, String imageLink, User user) {
        if ( (description == null || description.isBlank()) && (imageLink == null || imageLink.isBlank()) )
            throw new IllegalArgumentException("Description and imageLink cannot be null simultaneously");
        
        this.description = description;
        this.imageLink = imageLink;
        this.user = user;
        this.date = LocalDateTime.now();
        this.edited = false;
    }

    public void updateDescription(String description) {
        if (this.imageLink == null || this.imageLink.isBlank()) {
            if (description == null || description.isBlank()) 
                throw new IllegalArgumentException("Can't add an empty comment to a post without an image");
        }
        
        this.description = description;
        this.date = LocalDateTime.now();
        this.edited = true;
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    @Override
    public String toString() {
        return "Publication[" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", imageLink='" + imageLink + '\'' +
                ", date=" + date +
                ", edited=" + edited +
                ", user=" + user +
                ']';
    }
}	