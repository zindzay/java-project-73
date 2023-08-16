package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserDto(
        @NotBlank(message = "First name is required")
        @Size(min = 1, max = 30, message = "Your first name needs to be between 1 and 30 characters long")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Size(min = 1, max = 30, message = "Your last name needs to be between 1 and 30 characters long")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Please enter a valid email address")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 3, max = 30, message = "Your password needs to be between 3 and 30 characters long")
        String password) {
}
