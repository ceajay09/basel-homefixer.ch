package com.example.myproject.contact;

import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice(assignableTypes=ContactController.class)
public class ContactErrors {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ContactController.Result> validation(MethodArgumentNotValidException error) {
        Map<String, String> fields = new HashMap<>();
        error.getBindingResult().getFieldErrors().forEach(field -> fields.put(field.getField(), "invalid"));
        return ResponseEntity.badRequest().body(new ContactController.Result("invalid_request", fields));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ContactController.Result> malformed() {
        return ResponseEntity.badRequest().body(new ContactController.Result("invalid_request", Map.of()));
    }
}
