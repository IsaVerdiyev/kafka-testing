package isa.learning.handler;

import isa.learning.exception.ErrorDto;
import isa.learning.exception.RecordNotFoundException;
import isa.learning.logger.MainLogger;
import feign.RetryableException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    public static final String VALIDATION_FAILED_MSG = "Validation failed";
    public static final String SERVICE_TIMED_OUT_MSG = "Service timed out";
    public static final String RECORD_NOT_FOUND_MSG = "Record not found";
    public static final String INTERNAL_ERROR_MSG = "Internal error";
    public static final String LOG_MSG_TMP = "###EXCEPTION LOG###\n %s";
    private static final MainLogger LOG = MainLogger.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        LOG.error(String.format(LOG_MSG_TMP, getStackTrace(ex)));
        ConstraintViolationException exception = new ConstraintViolationException(getErrorDetail(ex), null);
        return buildResponseEntity(exception, request);
    }

    @ExceptionHandler({CompletionException.class, ExecutionException.class})
    public ResponseEntity<Object> handleCompletionException(Exception ex, WebRequest request) {
        LOG.error(String.format(LOG_MSG_TMP, getStackTrace(ex)));
        Throwable causeException = ex.getCause();
        while (causeException.getCause() != null
                && (causeException instanceof CompletionException || causeException instanceof ExecutionException)) {
            causeException = causeException.getCause();
        }
        return buildResponseEntity(causeException, request);
    }

    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> handleExceptions(Throwable ex, WebRequest request) {
        LOG.error(String.format(LOG_MSG_TMP, getStackTrace(ex)));
        return buildResponseEntity(ex, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            @Nullable Object body,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {

        return getResponseEntity(ex, status, request, VALIDATION_FAILED_MSG);
    }

    private ResponseEntity<Object> buildResponseEntity(Throwable ex, WebRequest request) {
        String message;
        HttpStatus status;
        if (ex instanceof ValidationException || ex instanceof ConstraintViolationException) {
            status = HttpStatus.BAD_REQUEST;
            message = VALIDATION_FAILED_MSG;
        } else if (ex instanceof RecordNotFoundException) {
            status = HttpStatus.NOT_FOUND;
            message = RECORD_NOT_FOUND_MSG;
        } else if (ex instanceof ResourceAccessException
                || ex instanceof RetryableException) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            message = SERVICE_TIMED_OUT_MSG;
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            message = INTERNAL_ERROR_MSG;
        }
        return getResponseEntity(ex, status, request, message);
    }

    private ResponseEntity<Object> getResponseEntity(
            Throwable ex, HttpStatus status, WebRequest request, String message) {

        ErrorDto errorResponseDto = ErrorDto.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .errorDetail(ex.getMessage())
                .path(((ServletWebRequest) request).getRequest().getRequestURI())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(errorResponseDto, status);
    }

    private String getErrorDetail(MethodArgumentNotValidException ex) {
        try {
            return ex.getBindingResult().getFieldError().getDefaultMessage();
        } catch (NullPointerException e) {
            return ex.getMessage();
        }
    }

    private String getStackTrace(Throwable e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

}
