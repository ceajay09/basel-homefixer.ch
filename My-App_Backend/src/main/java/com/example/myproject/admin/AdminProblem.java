package com.example.myproject.admin;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public final class AdminProblem extends ResponseStatusException {
    private final String code;
    public AdminProblem(int status, String code) { super(HttpStatus.valueOf(status), code); this.code=code; }
    public String code() { return code; }
    public static void require(boolean condition, String code) { if(!condition) throw new AdminProblem(422,code); }
}
