package proyecto.controller;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> validation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage()).collect(Collectors.joining("; "));
        return problem(HttpStatus.BAD_REQUEST, message);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String,Object>> invalidCredentials(BadCredentialsException ex) {
        return problem(HttpStatus.UNAUTHORIZED,"Credenciales invalidas");
    }
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String,Object>> status(ResponseStatusException ex) {
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("timestamp",Instant.now().toString());
        body.put("status",ex.getStatusCode().value());
        body.put("message",ex.getReason());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,Object>> integrity(DataIntegrityViolationException ex) {
        return problem(HttpStatus.CONFLICT,"Operacion incompatible con datos relacionados");
    }
    private ResponseEntity<Map<String,Object>> problem(HttpStatus code,String message) {
        Map<String,Object> body = new LinkedHashMap<>();
        body.put("timestamp",Instant.now().toString());
        body.put("status",code.value());
        body.put("message",message);
        return ResponseEntity.status(code).body(body);
    }
}
