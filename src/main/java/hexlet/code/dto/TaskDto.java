package hexlet.code.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record TaskDto(
        @NotBlank(message = "Task name is required")
        @Size(min = 1, max = 200, message = "Task name needs to be between 1 and 200 characters long")
        String name,

        @Size(max = 1000, message = "Task description needs to be between 0 and 1000 characters long")
        String description,

        @NotNull(message = "Task status is required")
        Long taskStatusId,

        List<Long> labelIds,

        Long executorId) {
    public TaskDto {
        if (labelIds == null) {
            labelIds = List.of();
        }
    }

}
