package br.com.blogsanapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

import br.com.blogsanapi.domain.user.User;

public interface UserRepository extends JpaRepository<User, String> {
    UserDetails findByLogin(String login);
}
