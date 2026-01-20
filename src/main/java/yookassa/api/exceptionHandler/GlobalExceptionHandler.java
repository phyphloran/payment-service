package yookassa.api.exceptionHandler;


import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import yookassa.api.dtos.ErrorDto;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<ErrorDto> paymentProcessingException(PaymentProcessingException exception) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDto(List.of(exception.getMessage())));
    }

    @ExceptionHandler(IncorrectIpException.class)
    public ResponseEntity<ErrorDto> incorrectIpException(IncorrectIpException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDto(List.of(exception.getMessage())));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleValidationException(MethodArgumentNotValidException exception) {
        List<String> errorMessages = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(new ErrorDto(errorMessages));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorDto> constraintViolationException(ConstraintViolationException exception) {
        List<String> errorMessages = exception.getConstraintViolations()
                .stream()
                .map(constraintViolation -> constraintViolation.getMessage())
                .collect(Collectors.toList());
        return ResponseEntity.badRequest().body(new ErrorDto(errorMessages));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorDto> handleMissingHeader(MissingRequestHeaderException ex) {
        String message = String.format("Required header '%s' is missing", ex.getHeaderName());
        return ResponseEntity.badRequest().body(new ErrorDto(List.of(message)));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ErrorDto> httpClientErrorException(HttpClientErrorException exception) {
        log.error("YooKassa client error: status={}, body={}",
                exception.getStatusCode(), exception.getResponseBodyAsString(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto(List.of("Unexpected error, please try again later")));
    }

    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ErrorDto> httpServerErrorException(HttpServerErrorException exception) {
        log.error("YooKassa server error: status={}, body={}",
                exception.getStatusCode(), exception.getResponseBodyAsString(), exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto(List.of("Unexpected error, please try again later")));
    }

    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ErrorDto> resourceAccessException(ResourceAccessException exception) {
        log.error("YooKassa connection error", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto(List.of("Unexpected error, please try again later")));
    }

}
