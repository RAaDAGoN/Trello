package dev.radagon.trello.service;

import dev.radagon.trello.dto.UserDTO;
import dev.radagon.trello.entity.User;
import dev.radagon.trello.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(UserDTO userDTO) {
        User user = User.builder()
                .email(userDTO.getEmail())
                .password(passwordEncoder.encode(userDTO.getPassword()))
                .userName(userDTO.getUsername())
                .createdAt(LocalDate.now().toString())
                .build();

        return userRepository.save(user);
    }

    public List<User> getAllUsers() {return userRepository.findAll();}

    public User findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID is null");
        }

        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User update(UserDTO userDTO) {
        if (userDTO.getId() != null) {
            User user = userRepository.findById(userDTO.getId()).orElseThrow(() -> new RuntimeException("User not found"));

            user.setEmail(userDTO.getEmail());
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            user.setUserName(userDTO.getUsername());

            return userRepository.save(user);
        } else {
            return createUser(userDTO);
        }
    }

    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID is null");
        }

        userRepository.deleteById(id);
    }
}
