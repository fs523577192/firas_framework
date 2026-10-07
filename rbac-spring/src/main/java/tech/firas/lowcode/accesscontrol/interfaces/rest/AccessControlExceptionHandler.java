package tech.firas.lowcode.accesscontrol.interfaces.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import tech.firas.lowcode.accesscontrol.domain.AccessControlErrorType;
import tech.firas.lowcode.accesscontrol.domain.AccessControlException;

@RestControllerAdvice
public class AccessControlExceptionHandler {

    @ExceptionHandler(AccessControlException.class)
    ResponseEntity<ApiError> handleAccessControlViolation(AccessControlException exception) {
        return respond(toStatus(exception.errorType()), exception);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ApiError> handleRejectedArgument(IllegalArgumentException exception) {
        return respond(HttpStatus.BAD_REQUEST, exception);
    }

    private HttpStatus toStatus(AccessControlErrorType errorType) {
        return switch (errorType) {
            case NOT_FOUND -> HttpStatus.NOT_FOUND;
            case CONFLICT -> HttpStatus.CONFLICT;
            case BAD_REQUEST -> HttpStatus.BAD_REQUEST;
        };
    }

    private ResponseEntity<ApiError> respond(HttpStatus status, RuntimeException exception) {
        return ResponseEntity.status(status)
                .body(new ApiError(status.value(), status.getReasonPhrase(), exception.getMessage()));
    }
}
