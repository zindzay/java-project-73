package hexlet.code.controller;

import hexlet.code.dto.ErrorDto;
import hexlet.code.dto.StatusDto;
import hexlet.code.model.Status;
import hexlet.code.service.StatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + StatusController.STATUS_CONTROLLER_PATH)
public class StatusController {

    public static final String STATUS_CONTROLLER_PATH = "/statuses";
    public static final String ID = "/{id}";

    private final StatusService statusService;

    @Operation(summary = "Get all statuses")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "422", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    @GetMapping
    public ResponseEntity<List<Status>> getAllStatuses() {
        return ResponseEntity.ok().body(statusService.findAllStatuses());
    }

    @Operation(summary = "Get status by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "422", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    @GetMapping(ID)
    public ResponseEntity<Status> getStatusById(@PathVariable final Long id) {
        return ResponseEntity.ok().body(statusService.findStatusById(id));
    }

    @Operation(summary = "Create new status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201"),
        @ApiResponse(responseCode = "422", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    @PostMapping
    public ResponseEntity<Status> createStatus(@RequestBody @Valid final StatusDto dto) {
        final Status status = statusService.createStatus(dto);
        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest()
                        .path(ID).buildAndExpand(status.getId()).toUri())
                .body(status);
    }

    @Operation(summary = "Update status by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "422", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    @PutMapping(ID)
    public ResponseEntity<Status> updateStatusById(@PathVariable final long id,
                                                 @RequestBody @Valid final StatusDto dto) {
        return ResponseEntity.ok().body(statusService.updateStatusById(id, dto));
    }

    @Operation(summary = "Delete status by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "422", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    @DeleteMapping(ID)
    public void deleteStatusById(@PathVariable final long id) {
        statusService.deleteStatusById(id);
    }

}
