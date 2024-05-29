package br.com.blogsanapi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import br.com.blogsanapi.repository.UserRepository;

@SpringBootApplication
public class BlogSanApiApplication {
	
	@Autowired
	private UserRepository repository;
	
	public static void main(String[] args) {
		SpringApplication.run(BlogSanApiApplication.class, args);
	}
}