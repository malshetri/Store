package com.muneer.store.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {
    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name musst be not more than 255 charachters")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email musst be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 25, message = "Password musst be between 6 and 25 charachters long")
    private String password;
}
