package dev.radagon.trello.config;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.nio.file.AccessDeniedException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public String handleResponseStatusException(ResponseStatusException e, Model model) {
        int statusCode = e.getStatusCode().value();
        log.warn("ResponseStatusException: {} - {} (reason: {})",
                statusCode, e.getMessage(), e.getReason());

        model.addAttribute("errorCode", String.valueOf(statusCode));
        model.addAttribute("errorMessage", e.getReason() != null ? e.getReason() : e.getMessage());

        return switch (statusCode) {
            case 400 -> "error/400";
            case 401 -> "error/401";
            case 403 -> "error/403";
            case 404 -> "error/404";
            case 500 -> "error/500";
            default -> "error/500";
        };
    }

    /**
     * 404
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFound(EntityNotFoundException e, Model model) {
        log.warn("Entity Not Found: {}", e.getMessage());
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorMessage", e.getMessage());

        return "error/404";
    }

    /**
     * 403 - нет доступа
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException e, Model model) {
        log.warn("Access denied: {}", e.getMessage());
        model.addAttribute("errorCode", "403");
        model.addAttribute("errorMessage", "Нет прав для выполнения этого действия");

        return "error/403";
    }

    /**
     * 401 - некорректные учетные данные
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleBadCredential(BadCredentialsException e, Model model) {
        log.warn("Bad credential");
        model.addAttribute("errorCode", "401");

        return "error/401";
    }

    /**
     * 400 - ошибка валидации формы
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationErrors(BindException e, Model model) {
        log.warn("Validation error: {}", e.getMessage());
        model.addAttribute("errorCode", "400");
        model.addAttribute("errorMessage", "Проверьте правильность введенных данных");

        return "error/400";
    }

    /**
     * 400 - Неверный тип параметра
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException e, Model model) {
        log.warn("Type mismatch for parameter {}: {}", e.getName(), e.getValue());
        model.addAttribute("errorCode", "400");
        model.addAttribute("errorMessage", "Некорректный формат данных URL");

        return "error/400";
    }

    /**
     * 404 - Нет обработчика для URL
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleTypeMismatch(NoHandlerFoundException e, Model model) {
        log.warn("No handler for URL: {}", e.getRequestURL());
        model.addAttribute("errorCode", "404");

        return "error/404";
    }

    /**
     * 500 - для всех других необработанных исключений
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllExceptions(Exception e, Model model) {
        log.error("Unexpected error", e);
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorMessage", e.getClass().getSimpleName());

        return "error/500";
    }
}
