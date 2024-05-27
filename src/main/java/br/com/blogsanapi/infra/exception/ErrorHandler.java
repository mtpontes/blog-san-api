package br.com.blogsanapi.infra.exception;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ErrorHandler {

	@ExceptionHandler(EntityNotFoundException.class)
	public ResponseEntity handleError404() {
		return ResponseEntity.notFound().build();
	}
	
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handlerMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    	List<FieldError> fieldErrors = ex.getFieldErrors();
    	
    	if (fieldErrors.isEmpty()) {
    	    String annotationMessage = ex.getBindingResult()
                    .getAllErrors()
                    .stream()
                    .filter(error -> error.getCode().equals("DescriptionAndImageLinkCannotBeBlank"))
                    .map(a -> a.getDefaultMessage())
                    .findFirst()
                    .orElse("");
    		
			return ResponseEntity.badRequest().body(new ErrorMessage(annotationMessage));
		}
    	
    	Map<String, String> fields = ex.getFieldErrors().stream()
    			.collect(Collectors.toMap(f -> f.getField().toString(), f -> f.getDefaultMessage()));
    	
    	var response = new ErrorMessageWithFields(
    			"Input validation error",
    			fields);
    	
    	return ResponseEntity
    			.badRequest()
    			.body(response);
    }
    
	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity handleIllegalArgument(IllegalArgumentException ex) {
		return ResponseEntity.badRequest().body(new ErrorMessage(ex.getMessage()));
	}
    // rever
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity handleError400(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(new ErrorMessage(ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity handleErrorBadCredentials() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessage("Invalid credentials"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity handleErrorAuthentication() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorMessage("Authentication failed"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity handleErrorAccessDenied() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorMessage("Access denied"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleError500(Exception ex) {
    	ex.printStackTrace();
    	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage("Internal server error"));
    }
    
    private record ErrorMessage(String error) {};
    private record ErrorMessageWithFields(String error, Object fields) {};
}