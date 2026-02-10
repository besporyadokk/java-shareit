package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(NotFoundException e) {
        log.error("NotFoundException: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflict(ConflictException e) {
        log.error("ConflictException: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler({BadRequestException.class, ValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(RuntimeException e) {
        log.error("BadRequest/Validation Exception: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalError(Exception e) {
        log.error("Internal Server Error: {}", e.getMessage(), e);
        return new ErrorResponse("Внутренняя ошибка сервера");
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDenied(AccessDeniedException e) {
        log.error("AccessDeniedException: {}", e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}