package br.com.blogsanapi.unit.security;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import br.com.blogsanapi.configs.UnityTest;
import br.com.blogsanapi.infra.exception.InvalidTokenException;
import br.com.blogsanapi.infra.security.TokenService;
import br.com.blogsanapi.model.user.User;

@UnityTest
public class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Value("${api.security.token.secret}")
    private String secret;
    
    private final User user = User.builder()
            .login("loginDefault")
            .build();


    @Test
    @DisplayName("Generate token and validate subject matches user login")
    void generateToken01() {
        // act
        String generatedToken = tokenService.generateToken(user);
        var recoveredSubject = tokenService.validateToken(generatedToken);
    
        // assert
        Assertions.assertEquals(user.getLogin(), recoveredSubject, "Recovered subject from token does not match user login");
    }
    
    @Test
    @DisplayName("Generated token subject matches user login and has expected expiration")
    void generateToken02() {
        // act
        String generatedToken = this.tokenService.generateToken(user);
        DecodedJWT decoded = this.decodeToken(generatedToken);
    
        // assert
        assertEquals(decoded.getSubject(), user.getLogin(), "Decoded token subject does not match user login");
        assertEquals(decoded.getExpiresAtAsInstant(), tokenService.getExpirationDateWhithoutMiliseconds(), "Token expiration does not match expected value");
    }
    
    @Test
    @DisplayName("Generate token throws IllegalArgumentException for null or blank login")
    void generateToken03() {
        // arrange
        
        // act and assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            tokenService.generateToken(User.builder().login(null).build());
        }, "Generating token with null login should throw IllegalArgumentException");
        
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            tokenService.generateToken(User.builder().login("").build());
        }, "Generating token with blank login should throw IllegalArgumentException");
    }
            

    @Test
    @DisplayName("Validate token throws InvalidTokenException with tampered tokens")
    void validateTokenTest01() {
      // arrange
      String generatedToken = tokenService.generateToken(user);
    
      // act and assert
      Assertions.assertAll(
          () -> Assertions.assertThrows(InvalidTokenException.class, () -> tokenService.validateToken(generatedToken.replace(".", ""))),
          () -> Assertions.assertThrows(InvalidTokenException.class, () -> tokenService.validateToken(generatedToken.substring(0, generatedToken.length() - 1))),
          () -> Assertions.assertThrows(InvalidTokenException.class, () -> {
            String repeatedChar = generatedToken.substring(generatedToken.length() - 1);
            tokenService.validateToken(generatedToken.replace(repeatedChar, repeatedChar + repeatedChar));
          })
      );
    }

    private DecodedJWT decodeToken(String token){
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm)
                .withIssuer("blog-san")
                .build()
                .verify(token);
    }
}