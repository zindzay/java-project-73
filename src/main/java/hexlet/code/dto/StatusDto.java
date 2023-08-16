package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record StatusDto(
        @NotBlank(message = "Status name is required")
        @Size(min = 1, max = 30, message = "Status name needs to be between 1 and 30 characters long")
        String name) {
}
