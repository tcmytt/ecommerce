package io.github.tcmytt.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import io.github.tcmytt.ecommerce.domain.response.ErrorResponse;

@RestControllerAdvice
public class GlobalException {

    // Handle all exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllException(Exception ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", ""); // Lấy đường dẫn request
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                path);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Handle NullPointerException
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointerException(NullPointerException ex, WebRequest request) {
        String path = request.getDescription(false).replace("uri=", ""); // Lấy đường dẫn request
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Null value encountered: " + ex.getMessage(),
                path);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex,
            WebRequest request) {
        String path = request.getDescription(false).replace("uri=", ""); // Lấy đường dẫn request
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Invalid argument: " + ex.getMessage(),
                path);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Handle custom exceptions (nếu có)
    // @ExceptionHandler(CustomException.class)
    // public ResponseEntity<ErrorResponse> handleCustomException(CustomException
    // ex, WebRequest request) {
    // String path = request.getDescription(false).replace("uri=", ""); // Lấy đường
    // dẫn request
    // ErrorResponse errorResponse = new ErrorResponse(
    // ex.getStatus(),
    // ex.getError(),
    // ex.getMessage(),
    // path
    // );
    // return new ResponseEntity<>(errorResponse,
    // HttpStatus.valueOf(ex.getStatus()));
    // }
}