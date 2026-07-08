package dev.radagon.trello.config;

import dev.radagon.trello.entity.User;
import dev.radagon.trello.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@AllArgsConstructor
public class UserInit implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail("user@mail.ru").isEmpty()) {
            User initUser = User.builder()
                    .email("user@mail.ru")
                    .password(passwordEncoder.encode("pas123"))
                    .userName("user")
                    .createdAt(LocalDate.now().toString())
                    .build();

            userRepository.save(initUser);
        }

        if (userRepository.findByEmail("user2@mail.ru").isEmpty()) {
            User initUser = User.builder()
                    .email("user2@mail.ru")
                    .password(passwordEncoder.encode("pas123"))
                    .userName("user")
                    .createdAt(LocalDate.now().toString())
                    .build();

            userRepository.save(initUser);
        }

    }
}
