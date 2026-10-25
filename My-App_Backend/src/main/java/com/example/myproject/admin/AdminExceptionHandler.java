package com.example.myproject.admin;

import java.util.Map;
import org.springframework.dao.*;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.*;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

@RestControllerAdvice(basePackageClasses=AdminContentController.class)
public class AdminExceptionHandler {
    private ResponseEntity<?> response(int status,String code){var builder=ResponseEntity.status(status).cacheControl(CacheControl.noStore());if(status==429)builder.header("Retry-After","2");return builder.body(Map.of("code",code));}
    @ExceptionHandler(AdminProblem.class) public ResponseEntity<?> problem(AdminProblem problem){return response(problem.getStatusCode().value(),problem.code());}
    @ExceptionHandler(DuplicateKeyException.class) public ResponseEntity<?> duplicate(DuplicateKeyException ignored){return response(409,"unique_conflict");}
    @ExceptionHandler(DataAccessException.class) public ResponseEntity<?> unavailable(DataAccessException ignored){return response(503,"content_store_unavailable");}
    @ExceptionHandler(MaxUploadSizeExceededException.class) public ResponseEntity<?> large(MaxUploadSizeExceededException ignored){return response(413,"upload_too_large");}
    @ExceptionHandler({HttpMessageNotReadableException.class,MissingServletRequestPartException.class,MultipartException.class})
    public ResponseEntity<?> invalid(Exception ignored){return response(400,"invalid_request");}
}
