package dev.radagon.trello.config;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * ResponseStatusException из контроллеров.
     * ВАЖНО: статус выставляется вручную, иначе страница придёт с HTTP 200.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public String handleResponseStatusException(ResponseStatusException e,
                                                Model model,
                                                HttpServletResponse response) {
        int statusCode = e.getStatusCode().value();
        log.warn("ResponseStatusException: {} - {}", statusCode, e.getReason());

        response.setStatus(statusCode); // ✅ корректный HTTP-статус

        model.addAttribute("errorCode", String.valueOf(statusCode));
        model.addAttribute("errorMessage",
                e.getReason() != null ? e.getReason() : e.getMessage());

        return switch (statusCode) {
            case 400 -> "error/400";
            case 401 -> "error/401";
            case 403 -> "error/403";
            case 404 -> "error/404";
            default -> "error/500";
        };
    }

    /** 404 — сущность не найдена в БД */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEntityNotFound(EntityNotFoundException e, Model model) {
        log.warn("Entity Not Found: {}", e.getMessage());
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorMessage", e.getMessage());
        return "error/404";
    }

    /** 403 — нет доступа */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDenied(AccessDeniedException e, Model model) {
        log.warn("Access denied: {}", e.getMessage());
        model.addAttribute("errorCode", "403");
        model.addAttribute("errorMessage", "Нет прав для выполнения этого действия");
        return "error/403";
    }

    /** 401 — почти мёртвый код (обычно ловится в фильтре), оставлен для надёжности */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String handleBadCredential(BadCredentialsException e, Model model) {
        log.warn("Bad credential: {}", e.getMessage());
        model.addAttribute("errorCode", "401");
        model.addAttribute("errorMessage", "Неверный email или пароль");
        return "error/401";
    }

    /** 400 — ошибка валидации формы */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationErrors(BindException e, Model model) {
        log.warn("Validation error: {}", e.getMessage());
        model.addAttribute("errorCode", "400");
        model.addAttribute("errorMessage", "Проверьте правильность введённых данных");
        return "error/400";
    }

    /** 400 — неверный тип параметра в URL */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleTypeMismatch(MethodArgumentTypeMismatchException e, Model model) {
        log.warn("Type mismatch for parameter {}: {}", e.getName(), e.getValue());
        model.addAttribute("errorCode", "400");
        model.addAttribute("errorMessage", "Некорректный формат данных URL");
        return "error/400";
    }

    /** 404 — нет обработчика (старый механизм) */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoHandlerFound(NoHandlerFoundException e, Model model) { // ✅ переименован
        log.warn("No handler for URL: {}", e.getRequestURL());
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorMessage", "Страница не найдена");
        return "error/404";
    }

    /** 404 — ресурс не найден (новый механизм Spring Boot 3.2+) */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNoResourceFound(NoResourceFoundException e, Model model) {
        log.warn("No resource found: {}", e.getResourcePath());
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorMessage", "Страница не найдена");
        return "error/404";
    }

    /** 500 — всё остальное */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleAllExceptions(Exception e, Model model) {
        log.error("Unexpected error", e);
        model.addAttribute("errorCode", "500");
        // не показываем пользователю внутренние детали (класс исключения)
        model.addAttribute("errorMessage", "Произошла непредвиденная ошибка");
        return "error/500";
    }
}