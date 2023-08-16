package hexlet.code;

import hexlet.code.dto.ErrorDto;
import hexlet.code.exeptions.UserNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorDto> handleNotFoundException(final UserNotFoundException exception) {
        final List<String> errors = List.of(exception.getMessage());

        return new ResponseEntity<>(new ErrorDto(errors), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorDto> handleGeneralExceptions(final Exception exception) {
        final List<String> errors = List.of(exception.getMessage());

        return new ResponseEntity<>(new ErrorDto(errors), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<ErrorDto> handleRuntimeExceptions(final RuntimeException exception) {
        final List<String> errors = List.of(exception.getMessage());

        return new ResponseEntity<>(new ErrorDto(errors), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationErrors(final MethodArgumentNotValidException exception) {
        final List<String> errors = exception.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .filter(Objects::nonNull)
                .toList();

        return new ResponseEntity<>(new ErrorDto(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<ErrorDto> handleAccessDeniedException(final AccessDeniedException exception) {
        final List<String> errors = List.of(exception.getMessage());

        return new ResponseEntity<>(new ErrorDto(errors), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

}
