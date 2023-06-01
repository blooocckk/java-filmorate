package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse validationFailed(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        StringBuilder errorMessage = new StringBuilder();
        for (FieldError fieldError : fieldErrors) {
            errorMessage.append(fieldError.getDefaultMessage()).append("; ");
        }

        log.error("Произошла ошибка с кодом {}. Стек-трейс: {}. ",
                HttpStatus.BAD_REQUEST.value(),
                e.getStackTrace());
        return new ErrorResponse(errorMessage.toString());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({RuntimeException.class, ObjectNotFoundException.class})
    public ErrorResponse notFound(RuntimeException e) {
        log.error("Произошла ошибка с кодом {}. Стек-трейс: {}. ",
                HttpStatus.NOT_FOUND.value(),
                e.getStackTrace());
        return new ErrorResponse(e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse internalServerError(Throwable e) {
        log.error("Произошла ошибка с кодом {}. Стек-трейс: {}. ",
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e.getStackTrace());
        return new ErrorResponse(e.getMessage());
    }
}