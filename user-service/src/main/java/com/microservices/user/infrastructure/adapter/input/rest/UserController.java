package com.microservices.user.infrastructure.adapter.input.rest;

import com.microservices.user.application.dto.CreateUserRequest;
import com.microservices.user.application.dto.UpdateUserRequest;
import com.microservices.user.application.dto.UserResponse;
import com.microservices.user.application.usecase.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 🌐 USER CONTROLLER - Adaptador de Entrada REST
 * 
 * Este es el punto de entrada HTTP para las operaciones de usuarios.
 * Es un "Adaptador de Entrada" en la Arquitectura Hexagonal.
 * 
 * Responsabilidades:
 * 1. Recibir peticiones HTTP
 * 2. Validar formato de datos (@Valid)
 * 3. Delegar a los casos de uso
 * 4. Convertir respuestas a HTTP
 * 5. Manejar códigos de estado HTTP
 * 
 * NO debe contener lógica de negocio.
 * 
 * Endpoints:
 * - POST   /api/users          → Crear usuario
 * - GET    /api/users          → Listar usuarios
 * - GET    /api/users/{id}     → Obtener usuario por ID
 * - PUT    /api/users/{id}     → Actualizar usuario
 * - DELETE /api/users/{id}     → Eliminar usuario
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    
    // Inyección de casos de uso (puertos de entrada)
    private final CreateUserUseCase createUserUseCase;
    private final FindUserByIdUseCase findUserByIdUseCase;
    private final FindAllUsersUseCase findAllUsersUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    
    /**
     * 📝 Crear un nuevo usuario
     * 
     * POST /api/users
     * 
     * Body:
     * {
     *   "email": "john@example.com",
     *   "name": "John Doe"
     * }
     * 
     * Response: 201 Created
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        log.info("📨 POST /api/users - Crear usuario: {}", request.email());
        
        UserResponse response = createUserUseCase.execute(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)  // 201
                .body(response);
    }
    
    /**
     * 📋 Obtener todos los usuarios
     * 
     * GET /api/users
     * 
     * Response: 200 OK
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        log.info("📨 GET /api/users - Obtener todos los usuarios");
        
        List<UserResponse> users = findAllUsersUseCase.execute();
        
        return ResponseEntity.ok(users);  // 200
    }
    
    /**
     * 🔍 Obtener usuario por ID
     * 
     * GET /api/users/{id}
     * 
     * Response: 200 OK
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        log.info("📨 GET /api/users/{} - Obtener usuario por ID", id);
        
        UserResponse user = findUserByIdUseCase.execute(id);
        
        return ResponseEntity.ok(user);  // 200
    }
    
    /**
     * ✏️ Actualizar usuario
     * 
     * PUT /api/users/{id}
     * 
     * Body:
     * {
     *   "email": "newemail@example.com",
     *   "name": "New Name"
     * }
     * 
     * Response: 200 OK
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UpdateUserRequest request
    ) {
        log.info("📨 PUT /api/users/{} - Actualizar usuario", id);
        
        UserResponse user = updateUserUseCase.execute(id, request);
        
        return ResponseEntity.ok(user);  // 200
    }
    
    /**
     * 🗑️ Eliminar usuario (soft delete)
     * 
     * DELETE /api/users/{id}
     * 
     * Response: 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        log.info("📨 DELETE /api/users/{} - Eliminar usuario", id);
        
        deleteUserUseCase.execute(id);
        
        return ResponseEntity.noContent().build();  // 204
    }
}
