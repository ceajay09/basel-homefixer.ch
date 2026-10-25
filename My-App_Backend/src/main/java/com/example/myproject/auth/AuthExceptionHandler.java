package com.example.myproject.auth;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.dao.DataAccessException;

@RestControllerAdvice(assignableTypes = AuthController.class)
public class AuthExceptionHandler {
    @ExceptionHandler(AuthFailure.class)
    public ResponseEntity<AuthDtos.Result> failure(AuthFailure error) {
        var builder = ResponseEntity.status(error.status()).cacheControl(CacheControl.noStore());
        if (error.retryAfter() > 0) builder.header("Retry-After", Long.toString(error.retryAfter()));
        if (error.status() == 401) builder.header("WWW-Authenticate", "Bearer");
        return builder.body(new AuthDtos.Result(error.code(), error.getMessage()));
    }
    @ExceptionHandler({MethodArgumentNotValidException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<AuthDtos.Result> invalid(Exception ignored) {
        return ResponseEntity.badRequest().cacheControl(CacheControl.noStore())
            .body(new AuthDtos.Result("invalid_request", "Please check the supplied fields."));
    }
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<AuthDtos.Result> unavailable(DataAccessException ignored) {
        return ResponseEntity.status(503).cacheControl(CacheControl.noStore())
            .body(new AuthDtos.Result("authentication_unavailable", "Account access is currently unavailable."));
    }
}
