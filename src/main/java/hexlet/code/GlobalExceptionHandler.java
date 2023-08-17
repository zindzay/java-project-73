package hexlet.code;

import com.rollbar.notifier.Rollbar;
import hexlet.code.dto.ErrorDto;
import hexlet.code.exeption.LabelNotFoundException;
import hexlet.code.exeption.StatusNotFoundException;
import hexlet.code.exeption.TaskNotFoundException;
import hexlet.code.exeption.UserNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@AllArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Rollbar rollbar;

    @ExceptionHandler(value = {UserNotFoundException.class, StatusNotFoundException.class,
        TaskNotFoundException.class, LabelNotFoundException.class})
    public ResponseEntity<ErrorDto> handleNotFoundException(final RuntimeException exception) {
        final List<String> errors = List.of(exception.getMessage());
        rollbar.error(exception.getMessage());
        return new ResponseEntity<>(new ErrorDto(errors), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ErrorDto> handleGeneralExceptions(final Exception exception) {
        final List<String> errors = List.of(exception.getMessage());
        rollbar.error(exception.getMessage());
        return new ResponseEntity<>(new ErrorDto(errors), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<ErrorDto> handleRuntimeExceptions(final RuntimeException exception) {
        final List<String> errors = List.of(exception.getMessage());
        rollbar.error(exception.getMessage());
        return new ResponseEntity<>(new ErrorDto(errors), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<FieldError>> handleValidationErrors(final MethodArgumentNotValidException exception) {
        return new ResponseEntity<>(exception.getBindingResult().getFieldErrors(), new HttpHeaders(),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<ErrorDto> handleAccessDeniedException(final AccessDeniedException exception) {
        final List<String> errors = List.of(exception.getMessage());
        rollbar.error(exception.getMessage());
        return new ResponseEntity<>(new ErrorDto(errors), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

}
