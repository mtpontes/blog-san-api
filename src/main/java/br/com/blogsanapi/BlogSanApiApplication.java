package br.com.blogsanapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import br.com.blogsanapi.model.user.User;
import br.com.blogsanapi.model.user.UserRole;
import br.com.blogsanapi.repository.UserRepository;

@SpringBootApplication
public class BlogSanApiApplication implements CommandLineRunner {
	
	@Autowired
	private UserRepository repository;
	
	public static void main(String[] args) {
		SpringApplication.run(BlogSanApiApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		if(!repository.existsByLogin("root")) {
			User userAdmin = User.builder()
								.login("root")
								.password(new BCryptPasswordEncoder().encode("root"))
								.role(UserRole.ADMIN)
								.build();

			repository.save(userAdmin);
		}
	}
}