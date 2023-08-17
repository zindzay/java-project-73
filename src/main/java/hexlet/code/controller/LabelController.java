package hexlet.code.controller;

import hexlet.code.dto.ErrorDto;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.service.LabelService;
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
@RequestMapping("${base-url}" + LabelController.LABEL_CONTROLLER_PATH)
public class LabelController {

    public static final String LABEL_CONTROLLER_PATH = "/labels";
    public static final String ID = "/{id}";

    private final LabelService labelService;

    @Operation(summary = "Get all labels")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "422", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    @GetMapping
    public ResponseEntity<List<Label>> getAllLabels() {
        return ResponseEntity.ok().body(labelService.findAllLabels());
    }

    @Operation(summary = "Get label by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "422", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    @GetMapping(ID)
    public ResponseEntity<Label> getLabelById(@PathVariable final Long id) {
        return ResponseEntity.ok().body(labelService.findLabelById(id));
    }

    @Operation(summary = "Create new label")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201"),
        @ApiResponse(responseCode = "422", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    @PostMapping
    public ResponseEntity<Label> createLabel(@RequestBody @Valid final LabelDto dto) {
        final Label label = labelService.createLabel(dto);
        return ResponseEntity
                .created(ServletUriComponentsBuilder.fromCurrentRequest()
                        .path(ID).buildAndExpand(label.getId()).toUri())
                .body(label);
    }

    @Operation(summary = "Update label by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "422", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    @PutMapping(ID)
    public ResponseEntity<Label> updateLabelById(@PathVariable final long id,
                                                 @RequestBody @Valid final LabelDto dto) {
        return ResponseEntity.ok().body(labelService.updateLabelById(id, dto));
    }

    @Operation(summary = "Delete label by id")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200"),
        @ApiResponse(responseCode = "401", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "403", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "404", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
        @ApiResponse(responseCode = "422", content = @Content(schema = @Schema(implementation = String.class))),
        @ApiResponse(responseCode = "500", content = @Content(schema = @Schema(implementation = ErrorDto.class))),
    })
    @DeleteMapping(ID)
    public void deleteLabelById(@PathVariable final long id) {
        labelService.deleteLabelById(id);
    }

}
