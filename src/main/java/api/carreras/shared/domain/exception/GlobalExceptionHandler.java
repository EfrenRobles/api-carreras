package api.carreras.shared.domain.exception;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import api.carreras.shared.domain.response.OnResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Object> serviceErrorHandler(Exception e) {
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());

        return OnResponse.onError(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(RepositoryException.class)
    public ResponseEntity<Object> reposirotyErrorHandler(Exception e) {
        List<String> errors = new ArrayList<>();
        errors.add(e.getMessage());

        return OnResponse.onError(errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // error handle for @Valid
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        List<String> errors = ex
            .getBindingResult()
            .getFieldErrors()
            .stream()
            .map(x -> x.getField() + ": " + x.getDefaultMessage())
            .collect(Collectors.toList());

        return OnResponse.onError(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
        TypeMismatchException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        List<String> errors = new ArrayList<>();
        errors.add(ex.getErrorCode() + ": " + ex.getRequiredType() + " by " + ex.getValue());

        return OnResponse.onError(errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }
}
