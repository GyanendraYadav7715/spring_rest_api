package com.codewithmosh.store.dtos;


import com.codewithmosh.store.validations.Lowercase;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterUserRequest {
    @NotBlank(message = "Name must be required.")
    @Size(max=255, message = "Name character must be under 255 letter.")
    private String name;
    @NotBlank(message = "Email must be required.")
    @Email
    @Lowercase(message = "Email must be in lowercase")
    private String email;
    @NotBlank(message = "Password must be required.")
    @Size(min = 6,max = 16,message = "Password must be between 6 -16 character long.")
    private String password;
}
