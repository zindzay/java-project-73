package hexlet.code;

import com.rollbar.notifier.Rollbar;
import hexlet.code.exeption.NotFoundServiceException;
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

    @ExceptionHandler(NotFoundServiceException.class)
    public ResponseEntity<String> handleNotFoundException(final RuntimeException exception) {
        rollbar.error(exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<String> handleGeneralExceptions(final Exception exception) {
        rollbar.error(exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public final ResponseEntity<String> handleRuntimeExceptions(final RuntimeException exception) {
        rollbar.error(exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<FieldError>> handleValidationErrors(final MethodArgumentNotValidException exception) {
        return new ResponseEntity<>(exception.getBindingResult().getFieldErrors(), new HttpHeaders(),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<String> handleAccessDeniedException(final AccessDeniedException exception) {
        rollbar.error(exception.getMessage());
        return new ResponseEntity<>(exception.getMessage(), new HttpHeaders(), HttpStatus.FORBIDDEN);
    }

}
