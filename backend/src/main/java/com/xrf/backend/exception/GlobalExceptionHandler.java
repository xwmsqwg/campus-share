package com.xrf.backend.exception;

import com.xrf.backend.pojo.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result> validationException(
            MethodArgumentNotValidException ex
    ) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("请求参数错误");

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Result.error(400, message));
    }

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Result> apiException(ApiException ex) {
        int status = ex.getStatus().value();

        return ResponseEntity
                .status(ex.getStatus())
                .body(Result.error(status, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> exception(Exception ex) {
        ex.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(500, "服务器内部错误"));
    }
}
