package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.ErrorDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + TaskController.TASK_CONTROLLER_PATH)
public class TaskController {

    public static final String TASK_CONTROLLER_PATH = "/tasks";
    public static final String ID = "/{id}";

    private static final String ONLY_TASK_OWNER_BY_ID = """
                @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
            """;

    private final TaskService taskService;

    @Operation(summary = "Get all tasks")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "422", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    @GetMapping
    public ResponseEntity<Iterable<Task>> getAllTasks(@QuerydslPredicate(root = Task.class) Predicate predicate) {
        return ResponseEntity.ok().body(taskService.findAllTasks(predicate));
    }

    @Operation(summary = "Get task by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "422", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    @GetMapping(ID)
    public ResponseEntity<Task> getTaskById(@PathVariable final Long id) {
        return ResponseEntity.ok().body(taskService.findTaskById(id));
    }

    @Operation(summary = "Create new task")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201"),
        @ApiResponse(responseCode = "422", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody @Valid final TaskDto dto) {
        final Task task = taskService.createTask(dto);
        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest()
                        .path(ID).buildAndExpand(task.getId()).toUri())
                .body(task);
    }

    @Operation(summary = "Update task by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "422", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    @PutMapping(ID)
    public ResponseEntity<Task> updateTaskById(@PathVariable final long id,
                                               @RequestBody @Valid final TaskDto dto) {
        return ResponseEntity.ok().body(taskService.updateTaskById(id, dto));
    }

    @Operation(summary = "Delete task by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "422", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    @PreAuthorize(ONLY_TASK_OWNER_BY_ID)
    @DeleteMapping(ID)
    public void deleteTaskById(@PathVariable final long id) {
        taskService.deleteTaskById(id);
    }

}
