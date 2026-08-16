package dev.radagon.trello.config;

import dev.radagon.trello.entity.User;
import dev.radagon.trello.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Утилита централизованной проверки аутентификации.
 * Используется ВСЕМИ контроллерами вместо ручных проверок.
 */

@Component
@RequiredArgsConstructor
public class SecurityHelper {
    private final UserRepository userRepository;


    /**
     * Метод проверяет, что пользователь аутентифицирован
     * Проверяет, чтобы publicId совпадал с publicId пользователя
     * (необходимо, чтобы другие пользователи не могли получить доступ к доске другого пользователя)
     */
    public User getAuthenticatedUser(String publicId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }

        User currentUser = userRepository.findByEmail(authentication.getName()).orElse(null);

        if (currentUser == null) {
            return null;
        }

        if (!currentUser.getPublicId().equals(publicId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No access");
        }

        return currentUser;
    }
}
