package br.com.blogsanapi.infra.exception;

public class InvalidTokenException extends RuntimeException {
    
    public InvalidTokenException(Throwable cause) {
        super("Invalid token", cause);
    }
}