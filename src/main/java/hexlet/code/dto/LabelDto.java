package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LabelDto(
        @NotBlank(message = "Label name is required")
        @Size(min = 1, max = 30, message = "Label name needs to be between 1 and 30 characters long")
        String name) {
}
