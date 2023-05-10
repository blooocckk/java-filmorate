package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


import javax.validation.ValidationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ValidationException.class)
    public ErrorResponse validationFailed(ValidationException e) {
        log.error("Произошла ошибка с кодом {}. Стек-трейс: {}. ",
                HttpStatus.BAD_REQUEST.value(),
                e.getStackTrace());
        return new ErrorResponse(e.getMessage());
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