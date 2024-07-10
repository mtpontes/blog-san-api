package br.com.blogsanapi.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {
    
    @Autowired
    SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return  httpSecurity
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/v3/api-docs/**", "/swagger-ui.html/**", "/swagger-ui/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/login", "/auth/register").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/admin/register").hasRole("ADMIN")
                        
                .requestMatchers(HttpMethod.POST, "/publications").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/publications/{publicationId}").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/publications/{id}").hasRole("ADMIN")
                        
                .requestMatchers(HttpMethod.GET, "/publications/**").permitAll()

                .requestMatchers(HttpMethod.POST, "/publications/{publicationId}/comments").hasAnyRole("ADMIN", "CLIENT")
                .requestMatchers(HttpMethod.POST, "/publications/comments/{targetCommentId}").hasAnyRole("ADMIN", "CLIENT")
                .requestMatchers(HttpMethod.PATCH, "/publications/comments/{commentId}").hasAnyRole("ADMIN", "CLIENT")
                .requestMatchers(HttpMethod.DELETE, "/publications/comments/{commentId}").hasAnyRole("ADMIN", "CLIENT")
                        
                .anyRequest().authenticated()
            )
            .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}