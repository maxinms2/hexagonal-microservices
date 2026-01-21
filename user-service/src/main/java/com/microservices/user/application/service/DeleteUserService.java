package com.microservices.user.application.service;

import com.microservices.user.domain.exception.UserNotFoundException;
import com.microservices.user.domain.model.User;
import com.microservices.user.domain.model.UserId;
import com.microservices.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 🗑️ DELETE USER SERVICE - Implementación del caso de uso
 */
@RequiredArgsConstructor
@Slf4j
public class DeleteUserService {

    private final UserRepository userRepository;

    public void execute(String userId) {
        log.info("🔹 Desactivando usuario: {}", userId);

        UserId id = UserId.of(userId);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(userId));

        user.deactivate();
        userRepository.save(user);

        log.info("✅ Usuario desactivado exitosamente");
    }
}
