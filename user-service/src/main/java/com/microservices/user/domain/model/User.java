package com.microservices.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 👤 USER - Entidad de Dominio
 * 
 * Esta es la entidad principal del dominio de usuarios.
 * Representa un usuario en nuestro sistema.
 * 
 * IMPORTANTE: Esta clase NO tiene anotaciones de JPA (@Entity)
 * porque pertenece al DOMINIO, no a la infraestructura.
 * 
 * El dominio debe ser independiente de frameworks y bases de datos.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    /**
     * Identificador único del usuario
     */
    private UserId id;
    
    /**
     * Email del usuario (único en el sistema)
     */
    private Email email;
    
    /**
     * Nombre completo del usuario
     */
    private String name;
    
    /**
     * Fecha de creación del usuario
     */
    private LocalDateTime createdAt;
    
    /**
     * Fecha de última actualización
     */
    private LocalDateTime updatedAt;
    
    /**
     * Indica si el usuario está activo
     */
    private boolean active;
    
    // ============================================
    // FACTORY METHOD: Crear nuevo usuario
    // ============================================
    
    /**
     * Crea un nuevo usuario con valores por defecto.
     * Este es un método de fábrica que garantiza que
     * los nuevos usuarios se crean de forma consistente.
     */
    public static User create(Email email, String name) {
        User user = new User();
        user.setId(UserId.generate());
        user.setEmail(email);
        user.setName(name);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setActive(true);
        return user;
    }
    
    // ============================================
    // BUSINESS LOGIC: Lógica de Negocio
    // ============================================
    
    /**
     * Actualiza el email del usuario.
     * Incluye validación de negocio.
     */
    public void updateEmail(Email newEmail) {
        if (newEmail.equals(this.email)) {
            throw new IllegalArgumentException("El nuevo email es igual al actual");
        }
        this.email = newEmail;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Actualiza el nombre del usuario.
     */
    public void updateName(String newName) {
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.name = newName.trim();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Desactiva el usuario.
     * Un usuario desactivado no puede hacer login.
     */
    public void deactivate() {
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Activa el usuario.
     */
    public void activate() {
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }
}
