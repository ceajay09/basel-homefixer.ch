package com.example.myproject.contact;

import jakarta.validation.constraints.*;

public record ContactRequest(
    @NotBlank @Size(min=2, max=100) @Pattern(regexp="[^\\r\\n]*") String name,
    @NotBlank @Email @Size(max=254) @Pattern(regexp="^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$") String email,
    @Size(max=40) @Pattern(regexp="[+0-9() .-]*") String phone,
    @Size(max=30) String service,
    @NotBlank @Size(min=10, max=5000) String message,
    @AssertTrue boolean consent,
    @Size(max=200) String website
) {
    public ContactRequest {
        if (name != null) name = name.trim();
        if (email != null) email = email.trim();
        if (phone != null) phone = phone.trim();
        if (message != null) message = message.trim();
    }
}
