package cat.copernic.backendProjecte3.api.common;

import cat.copernic.backendProjecte3.exceptions.AccesDenegatException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.HttpRequestMethodNotSupportedException ;

import java.time.Instant;

/**
 * Gestor global d'excepcions REST.
 * <p>
 * Converteix excepcions en respostes JSON consistents per a l'aplicació client.
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiError> handleApi(ApiException ex, HttpServletRequest req) {
        int status = ex.getStatus();
        ApiError body = new ApiError(
                Instant.now(),
                status,
                "API_ERROR",
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(AccesDenegatException.class)
    public ResponseEntity<ApiError> handleAcces(AccesDenegatException ex, HttpServletRequest req) {
        ApiError body = new ApiError(
                Instant.now(),
                401,
                "UNAUTHORIZED",
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(401).body(body);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        ApiError body = new ApiError(
                Instant.now(),
                405,
                "METHOD_NOT_ALLOWED",
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(405).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleOther(Exception ex, HttpServletRequest req) {
        ex.printStackTrace();

        ApiError body = new ApiError(
                Instant.now(),
                500,
                "INTERNAL_ERROR",
                ex.getMessage(),
                req.getRequestURI()
        );
        return ResponseEntity.status(500).body(body);
    }

}
