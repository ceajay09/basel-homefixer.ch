package com.example.myproject.admin;

import com.example.myproject.content.*;
import org.springframework.http.*;

final class AdminHeaders {
    private AdminHeaders() {}
    static long expected(String header) {
        if(header==null)throw new AdminProblem(428,"if_match_required");
        if(!header.matches("\"(0|[1-9][0-9]{0,15})\""))throw new AdminProblem(400,"invalid_if_match");
        return Long.parseLong(header.substring(1,header.length()-1));
    }
    static <T> ResponseEntity<T> response(T body,Long version) {
        return ResponseEntity.ok().cacheControl(CacheControl.noStore()).eTag(Long.toString(AdminContentStore.version(version))).body(body);
    }
}
