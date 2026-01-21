package com.microservices.user.application.service;

import com.microservices.user.application.dto.CreateUserRequest;
import com.microservices.user.application.dto.UpdateUserRequest;
import com.microservices.user.application.dto.UserResponse;
import com.microservices.user.domain.exception.EmailAlreadyExistsException;
import com.microservices.user.domain.exception.UserNotFoundException;
import com.microservices.user.domain.model.Email;
import com.microservices.user.domain.model.User;
import com.microservices.user.domain.model.UserId;
import com.microservices.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 🎯 USER SERVICE - Implementación de los Casos de Uso
 * 
 * Esta clase implementa TODOS los casos de uso relacionados con usuarios.
 * Contiene la lógica de aplicación (orquestación).
 *
 * Flujo:
 * Controller → UseCase (interface) → Service (implementación) → Repository
 * (interface) → DB
 */
@RequiredArgsConstructor
@Slf4j
public class UserService {

    // Dependencias inyectadas por constructor (gracias a @RequiredArgsConstructor)
    private final UserRepository userRepository;

    // ============================================
    // CREATE USER
    // ============================================

    public UserResponse execute(CreateUserRequest request) {
        log.info("🔹 Creando usuario con email: {}", request.email());

        // 1. Crear Value Object Email (con validación automática)
        Email email = new Email(request.email());

        // 2. Verificar que el email no exista
        if (userRepository.existsByEmail(email)) {
            log.warn("⚠️ Email ya existe: {}", email.value());
            throw new EmailAlreadyExistsException(email.value());
        }

        // 3. Crear entidad de dominio
        User user = User.create(email, request.name());

        // 4. Guardar en el repositorio
        User savedUser = userRepository.save(user);

        log.info("✅ Usuario creado exitosamente: {}", savedUser.getId());

        // 5. Convertir a DTO de respuesta
        return UserResponse.from(savedUser);
    }

    // ============================================
    // FIND USER BY ID
    // ============================================

    public UserResponse execute(String userId) {
        log.info("🔹 Buscando usuario con ID: {}", userId);

        // 1. Convertir String a UserId
        UserId id = UserId.of(userId);

        // 2. Buscar en el repositorio
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("⚠️ Usuario no encontrado: {}", userId);
                    return new UserNotFoundException(userId);
                });

        log.info("✅ Usuario encontrado: {}", user.getEmail().value());

        // 3. Convertir a DTO
        return UserResponse.from(user);
    }

    // ============================================
    // FIND ALL USERS
    // ============================================

    public List<UserResponse> execute() {
        log.info("🔹 Obteniendo todos los usuarios activos");

        // 1. Obtener usuarios del repositorio
        List<User> users = userRepository.findAllActive();

        log.info("✅ Se encontraron {} usuarios", users.size());

        // 2. Convertir a DTOs usando streams
        return users.stream()
                .map(UserResponse::from)
                .collect(Collectors.toList());
    }

    // ============================================
    // UPDATE USER
    // ============================================

    public UserResponse execute(String userId, UpdateUserRequest request) {
        log.info("🔹 Actualizando usuario: {}", userId);

        // 1. Buscar usuario existente
        UserId id = UserId.of(userId);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // 2. Actualizar email si se proporcionó
        if (request.email() != null) {
            Email newEmail = new Email(request.email());

            // Verificar que el nuevo email no exista
            if (userRepository.existsByEmail(newEmail)) {
                throw new EmailAlreadyExistsException(newEmail.value());
            }

            user.updateEmail(newEmail);
            log.info("📧 Email actualizado a: {}", newEmail.value());
        }

        // 3. Actualizar nombre si se proporcionó
        if (request.name() != null) {
            user.updateName(request.name());
            log.info("📝 Nombre actualizado a: {}", request.name());
        }

        // 4. Guardar cambios
        User updatedUser = userRepository.save(user);

        log.info("✅ Usuario actualizado exitosamente");

        return UserResponse.from(updatedUser);
    }

    // (El caso de uso de borrado se movió a DeleteUserService)
}
