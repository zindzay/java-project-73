package hexlet.code.exeption;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundServiceException extends RuntimeException {
    public NotFoundServiceException(final String message) {
        super(message);
    }
}
